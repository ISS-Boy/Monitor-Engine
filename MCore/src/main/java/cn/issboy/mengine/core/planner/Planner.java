package cn.issboy.mengine.core.planner;

import cn.issboy.mengine.core.analyzer.Analysis;
import cn.issboy.mengine.core.metastore.SchemadDataSource;
import cn.issboy.mengine.core.planner.plan.*;
import javafx.util.Pair;

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


    public OutputNode buildOutputNode(PlanNode parentNode) {

        return new OutputNode(parentNode,analysis.getMonitorId().toString());
    }

    public ProjectNode buildProjectNode(PlanNode parentNode) {


        return new ProjectNode(parentNode, analysis.getSelectFileds());

    }
    private ReMapNode buildReMapNode(PlanNode parentNode){
        return new ReMapNode(parentNode);
    }

    public AggregateNode buildAggNode(PlanNode parentNode) {
        return new AggregateNode(parentNode, analysis.getInitContext().toString(), analysis.getAggContext().toString(), analysis.getWindow());
    }

    public FilterNode buildFilterNode(PlanNode parentNode) {

        return new FilterNode(parentNode, analysis.getPredicates());
    }

    public SourceNode buildSourceNode() {

        Pair<SchemadDataSource, String> schemadDataSourceStringPair = analysis.getSources().get(0);
        return new SourceNode(schemadDataSourceStringPair.getKey());

    }


}
