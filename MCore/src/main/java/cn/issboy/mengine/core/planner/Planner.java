package cn.issboy.mengine.core.planner;

import cn.issboy.mengine.core.analyzer.Analysis;
import cn.issboy.mengine.core.metastore.SchemadDataSource;
import cn.issboy.mengine.core.planner.plan.*;
import cn.issboy.mengine.core.util.Pair;

/**
 * created by just on 18-1-3
 */
public class Planner {

    private Analysis analysis;

    public Planner(Analysis analysis) {
        this.analysis = analysis;
    }

    public PlanNode buildPlan() {

        PlanNode curNode;
        if (analysis.getJoin() != null) {
            curNode = analysis.getJoin();
        } else {
            curNode = buildSourceNode();
        }
        // analyze的时候应该记录一下是不是只有stream-stream join
        curNode = buildReMapNode(curNode);

        if (!analysis.getAggContext().toString().equals("")) {
            curNode = buildAggNode(curNode);
        }
        if (analysis.getPredicates().size() != 0) {
            curNode = buildFilterNode(curNode);
        }
        if (analysis.getSelectFileds().size() != 0) {
            curNode = buildProjectNode(curNode);
        }

        return buildOutputNode(curNode);
    }


    private OutputNode buildOutputNode(PlanNode parentNode) {

        return new OutputNode(parentNode,analysis.getMonitorId().toString());
    }

    private ProjectNode buildProjectNode(PlanNode parentNode) {


        return new ProjectNode(parentNode, analysis.getSelectFileds());

    }
    private ReMapNode buildReMapNode(PlanNode parentNode){
        return new ReMapNode(parentNode);
    }

    private AggregateNode buildAggNode(PlanNode parentNode) {
        return new AggregateNode(parentNode, analysis.getInitContext().toString(), analysis.getAggContext().toString(), analysis.getWindow());
    }

    private FilterNode buildFilterNode(PlanNode parentNode) {

        return new FilterNode(parentNode, analysis.getPredicates(),analysis.getMeasures());
    }

    private SourceNode buildSourceNode() {

        Pair<SchemadDataSource, String> schemadDataSourceStringPair = analysis.getSources().get(0);
        return new SourceNode(schemadDataSourceStringPair.getKey());

    }


}
