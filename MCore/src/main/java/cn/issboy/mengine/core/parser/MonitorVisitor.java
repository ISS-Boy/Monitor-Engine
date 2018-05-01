package cn.issboy.mengine.core.parser;

import cn.issboy.mengine.core.parser.*;

/**
 * created by just on 18-2-26
 */
public abstract class MonitorVisitor {

    public void process(Node node){
        node.accept(this);
    }

    public  void visitBlockGroup(BlockGroup node){;};

    public abstract void visitBlockValues(BlockValues node);

    public abstract void visitAggregation(Aggregation node);

    public abstract void visitAggregationValues(AggregationValues node);

    public abstract void visitFilters(Filters node);

    public abstract void visitSource(Source node);

    public abstract void visitWindow(Window node);

    public abstract void visitSelects(Selects node);

    public abstract void visitPredicates(Predicates node);

    public void visitNode(Node node){
        ;
    }

}
