package cn.issboy.mengine.core.analyzer;

import cn.issboy.mengine.core.metastore.DataSource;
import cn.issboy.mengine.core.metastore.MetaStore;
import cn.issboy.mengine.core.metastore.SchemadDataSource;
import cn.issboy.mengine.core.parser.*;
import cn.issboy.mengine.core.planner.plan.JoinNode;
import cn.issboy.mengine.core.planner.plan.PlanNode;
import cn.issboy.mengine.core.planner.plan.SourceNode;
import cn.issboy.mengine.core.util.SchemaUtil;
import cn.issboy.mengine.core.util.StringUtil;
import javafx.util.Pair;
import org.apache.avro.Schema;

import java.io.Console;
import java.util.List;
import java.util.Locale;
import java.util.Objects;


/**
 * created by just on 18-2-26
 */
public class Analyzer extends MonitorVisitor {

    private final Analysis analysis;
    MetaStore metaStore;

    public Analyzer(Analysis analysis, MetaStore metaStore) {
        this.analysis = analysis;
        this.metaStore = metaStore;
    }

    @Override
    public void visitBlockValues(BlockValues node) {
        analysis.getMonitorId().append("_")
                .append(node.getMonitorName());

        // 处理源数据
        List<Source> sources = node.getSource();
        for (Source source : sources) {
            process(source);
        }
        List<Pair<SchemadDataSource, String>> schemaSources = analysis.getSources();

        /** 多于两个数据源时进行join操作
         *
         *
         */
        if (schemaSources.size() >= 2) {
            int left = 0, right = schemaSources.size() - 1;
            DataSource.DataType lType = schemaSources.get(left).getKey().getDataType();
            DataSource.DataType rType = schemaSources.get(right).getKey().getDataType();
            boolean flag = lType.equals(rType);
            JoinNode.Type joinType = lType.equals(DataSource.DataType.STREAM) ? JoinNode.Type.SINNER : JoinNode.Type.TINNER;

            PlanNode leftNode = new SourceNode(schemaSources.get(left).getKey());
            PlanNode rightNode = new SourceNode(schemaSources.get(right).getKey());

            while (left < right - 1 &&
                    schemaSources.get(left).getKey().getDataType().equals(schemaSources.get(left + 1).getKey().getDataType())) {
                PlanNode tmpNode = new SourceNode(schemaSources.get(left++).getKey());
                leftNode = new JoinNode(joinType, leftNode, tmpNode);
            }
            while (left < right - 1 &&
                    schemaSources.get(right).getKey().getDataType().equals(schemaSources.get(right - 1).getKey().getDataType())) {
                PlanNode tmpNode = new SourceNode(schemaSources.get(right--).getKey());
                rightNode = new JoinNode(JoinNode.Type.TINNER, tmpNode, rightNode);
            }
            JoinNode joinNode;
            if (flag) {
                joinNode = new JoinNode(joinType, leftNode, rightNode);
            } else {
                joinNode = new JoinNode(JoinNode.Type.LEFT, leftNode, rightNode);
            }
            analysis.setJoin(joinNode);

        }
        // 处理聚集值
        // TODO : remove {node.getAggregation()...} after problem fixed in front end.
        if (node.getAggregation() != null
                && node.getAggregation().getAggregationValues() != null
                && !node.getAggregation().getAggregationValues().isEmpty()) {
            process(node.getAggregation());
        }

        // 处理过滤器
        List<Filters> filters = node.getFilters();
        for (Filters filter : filters) {
            process(filter);
        }

        // 处理选择结果(Project)
        List<Selects> selects = node.getSelects();
        for (Selects select : selects) {
            process(select);
        }
    }


    @Override
    public void visitSource(Source node) {
        String sourceName = node.getSourceName();
        SchemadDataSource schemadDataSource = metaStore.getDataSource(sourceName);
        analysis.addSources(new Pair<>(schemadDataSource, sourceName));
    }


    @Override
    public void visitAggregation(Aggregation node) {
        if (node.getWindow() != null) {
            process(node.getWindow());
        }

        List<AggregationValues> aggregationValues = node.getAggregationValues();

        for (AggregationValues agg : aggregationValues) {
            process(agg);
        }

    }

    @Override
    public void visitAggregationValues(AggregationValues node) {
        // "avg","average","systolic_blood_pressure","predicate",
        StringBuilder initializer = analysis.getInitContext();
        StringBuilder aggregator = analysis.getAggContext();

        String measure = node.getMeasure();
        String alias = node.getName();
        String funcName = node.getType();
        String source = node.getSource();
        // 聚集结果类型,目前默认和原值相同
        Schema.Type derivedType = metaStore.getDataSource(source).getSchema()
                .getField(measure).schema().getType();
        SchemadDataSource dataSource = metaStore.getDataSource(source);
        Schema newSchema = SchemaUtil.expandSchema(dataSource.getSchema(), alias, derivedType);
        metaStore.putDataSource(new SchemadDataSource(DataSource.DataType.TABLE, newSchema, source));

        initializer.append(StringUtil.wrapString(alias))
                .append(",")
                .append(StringUtil.wrapString(funcName))
                .append(",");

        aggregator.append(StringUtil.wrapString(alias))
                .append(",")
                .append(StringUtil.wrapString(funcName))
                .append(",")
                .append(StringUtil.wrapString(measure))
                .append(",");

        if (node.getPredicates() != null && !node.getPredicates().isEmpty()) {
            // systolic_blood_pressure > 120 && diastolic_blood_pressure > 80
            List<Predicates> predicates = node.getPredicates();
            int length = predicates.size();
            if (length > 1) {
                aggregator.append("\"");
                for (int i = 0; i < length; i++) {
                    Predicates predicate = predicates.get(i);
                    aggregator.append(predicate.getMeasure())
                            .append(predicate.getOp())
                            .append(predicate.getThreshold());
                    if (i != (length - 1)) {
                        aggregator.append(predicate.getBoolExp());
                    }
                }
                aggregator.append("\"");
            } else {
                process(predicates.get(0));
            }

        } else {
            // 占位
            aggregator.append(StringUtil.wrapString(""));
        }
        aggregator.append(",");

    }

    @Override
    public void visitWindow(Window node) {
        StringBuilder window = new StringBuilder();
        window.append("TimeWindows.of(TimeUnit.MINUTES.toMillis(");
        switch (node.getWindowType()) {
            // TimeWindows.of(TimeUnit.MINUTES.toMillis(5)).advanceBy(TimeUnit.MINUTES.toMillis(2))
            case "hopping":
                window.append(node.getWindowLength())
                        .append(")).advanceBy(TimeUnit.MINUTES.toMillis(")
                        .append(node.getWindowInterval())
                        .append("))");
                analysis.setWindow(window.toString());
                break;
            // TimeWindows.of(TimeUnit.MINUTES.toMillis(5))
            case "tumbling":
            default:
                window.append(node.getWindowLength())
                        .append("))");
                analysis.setWindow(window.toString());
                break;

        }
    }

    @Override
    public void visitPredicates(Predicates node) {
        // "systolic_blood_pressure > 120"
        analysis.getAggContext()
                .append("\"")
                .append(node.getMeasure())
                .append(node.getOp())
                .append(node.getThreshold())
                .append("\"");
    }

    @Override
    public void visitFilters(Filters node) {
        // (Float)v.getValues().get("systolic_blood_pressure")>120&&
        StringBuilder predicateBuilder = new StringBuilder();
        String threshold = node.getF_threshold();
        String topic = node.getF_source();
        String measure = node.getF_measure();
        String fieldSchema = metaStore.getDataSource(topic).getSchema()
                .getField(measure).schema().getType().toString();

        predicateBuilder.append("(")
                .append(StringUtil.lowerCase(fieldSchema))
                .append(")")
                .append("v.getValues().get(")
                .append(StringUtil.wrapString(measure))
                .append(")")
                .append(node.getF_op())
                .append(threshold)
                .append(StringUtil.toSymbol(node.getF_boolExp()));

        analysis.addPredicate(predicateBuilder.toString());
    }

    @Override
    public void visitSelects(Selects node) {
        // {alias0,alias1,...}
        analysis.addField(StringUtil.wrapString(node.getS_meaOrCal()));
    }


}
