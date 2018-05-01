package cn.issboy.mengine.core.parser;

/**
 * created by just on 18-4-9
 */
public abstract class Node {

    protected void accept(MonitorVisitor visitor){
        visitor.visitNode(this);
    }

}
