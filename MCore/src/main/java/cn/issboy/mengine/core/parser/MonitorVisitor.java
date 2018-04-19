package cn.issboy.mengine.core.parser;

import cn.issboy.mengine.core.parser.*;

/**
 * created by just on 18-2-26
 */
public abstract class MonitorVisitor {

    /**
     * analyze monitor block
     */
    public void process(Node node){
        node.accept(this);
    }

    public abstract void visitBlock(Block node);
    public abstract void visitCalculation(Calculation node);
    public abstract void visitCalculationValues(CalculationValues node);
    public abstract void visitFilters(Filters node);
    public abstract void visitSource(Source node);
    public abstract void visitWindow(Window node);
    public abstract void visitSelects(Selects node);
    public void visitNode(Node node){
        ;
    }



}
