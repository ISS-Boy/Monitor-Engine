package cn.issboy.mengine.core.analyzer;

import cn.issboy.mengine.core.MEngine;
import cn.issboy.mengine.core.exception.MException;
import cn.issboy.mengine.core.metastore.DataSource;
import cn.issboy.mengine.core.metastore.MetaStore;
import cn.issboy.mengine.core.metastore.SchemadDataSource;
import cn.issboy.mengine.core.parser.*;
import cn.issboy.mengine.core.planner.plan.JoinNode;
import cn.issboy.mengine.core.planner.plan.PlanNode;
import cn.issboy.mengine.core.planner.plan.SourceNode;
import cn.issboy.mengine.core.util.SchemaUtil;
import cn.issboy.mengine.core.util.StringUtils;
import cn.issboy.mengine.core.util.Pair;
import org.apache.avro.Schema;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;


/**
 * created by just on 18-2-26
 */
public class Analyzer extends MonitorVisitor {

    Logger logger = LoggerFactory.getLogger(this.getClass());

    private final Analysis analysis;
    MetaStore metaStore;

    public Analyzer(Analysis analysis, MetaStore metaStore) {
        this.analysis = analysis;
        this.metaStore = metaStore;
    }

    @Override
    public void visitBlockValues(BlockValues node) {
        analysis.getMonitorId().append("_")
                .append(MEngine.monitorSeqNum.getAndIncrement());


        // 处理数据源
        List<Source> sources = node.getSource();
        for (Source source : sources) {
            process(source);
        }
        List<Pair<SchemadDataSource, String>> schemaSources = analysis.getSources();

        /**
         * 多于两个数据源时进行join操作
         *
         */
        if (schemaSources.size() > 1) {
            int left = 0, right = schemaSources.size() - 1;
            DataSource.DataType lType = schemaSources.get(left).getKey().getDataType();
            DataSource.DataType rType = schemaSources.get(right).getKey().getDataType();
            boolean flag = lType.equals(rType);
            JoinNode.Type joinType = lType.equals(DataSource.DataType.STREAM) ? JoinNode.Type.SINNER : JoinNode.Type.TINNER;

            PlanNode leftNode = new SourceNode(schemaSources.get(left).getKey());
            PlanNode rightNode = new SourceNode(schemaSources.get(right).getKey());

            while (left < right - 1 &&
                   schemaSources.get(left).getKey().getDataType().equals(schemaSources.get(left + 1).getKey().getDataType())) {
                PlanNode tmpNode = new SourceNode(schemaSources.get(++left).getKey());
                leftNode = new JoinNode(joinType, leftNode, tmpNode);
            }
            while (left < right - 1 &&
                   schemaSources.get(right).getKey().getDataType().equals(schemaSources.get(right - 1).getKey().getDataType())) {
                PlanNode tmpNode = new SourceNode(schemaSources.get(--right).getKey());
                rightNode = new JoinNode(JoinNode.Type.TINNER, tmpNode, rightNode);
            }
            JoinNode joinNode;
            if (flag) {
                joinNode = new JoinNode(joinType, leftNode, rightNode);
            } else {
                joinNode = new JoinNode(JoinNode.Type.STINNER, leftNode, rightNode);
            }
            analysis.setJoin(joinNode);

        }
        // 处理聚集值
        // TODO : remove {node.getAggregation()...} after problem fixed in front end.
        if (node.getAggregation() != null &&
            node.getAggregation().getAggregationValues() != null &&
            !node.getAggregation().getAggregationValues().isEmpty()) {
            process(node.getAggregation());
        }

        // 处理过滤器
        if (node.getFilters() != null &&
            !node.getFilters().isEmpty()) {
            List<Filters> filters = node.getFilters();
            for (Filters filter : filters) {
                process(filter);
            }

        }

        // 处理选择结果(Project)
        if (node.getSelects() != null &&
            !node.getSelects().isEmpty()) {
            List<Selects> selects = node.getSelects();
            for (Selects select : selects) {
                process(select);
            }
        } else {
            logger.error("no select item provided");
            throw new IllegalStateException("please choose select block to complete monitor");
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

        initializer.append(StringUtils.wrapString(alias))
                .append(",")
                .append(StringUtils.wrapString(funcName))
                .append(",");

        aggregator.append(StringUtils.wrapString(alias))
                .append(",")
                .append(StringUtils.wrapString(funcName))
                .append(",")
                .append(StringUtils.wrapString(measure))
                .append(",");

        if (node.getPredicates() != null && !node.getPredicates().isEmpty()) {
            // systolic_blood_pressure > 120 && diastolic_blood_pressure > 80
            List<Predicates> predicates = node.getPredicates();

            int length = predicates.size();
            if (length > 1) {
                aggregator.append("\"");
                for (int i = 0; i < length; i++) {
                    Predicates predicate = predicates.get(i);
                    if (!predicate.getMeasure().equals(measure)) {
                        throw new IllegalStateException("please check your choice on aggregation values");
                    }
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
            aggregator.append(StringUtils.wrapString(""));
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
                .append(StringUtils.lowerCase(fieldSchema))
                .append(")")
                .append("v.getValues().get(")
                .append(StringUtils.wrapString(measure))
                .append(")")
                .append(node.getF_op())
                .append(threshold)
                .append(StringUtils.toSymbol(node.getF_boolExp()));

        analysis.addMeasure(measure);
        analysis.addPredicate(predicateBuilder.toString());
    }

    @Override
    public void visitSelects(Selects node) {
        // {alias0,alias1,...}
        analysis.addField(StringUtils.wrapString(node.getS_meaOrCal()));

    }


}
