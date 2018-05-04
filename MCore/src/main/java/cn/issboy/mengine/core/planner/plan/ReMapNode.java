package cn.issboy.mengine.core.planner.plan;

import cn.issboy.mengine.core.codegen.MonitorKStreamBuilder;
import org.apache.avro.Schema;

import java.util.Map;

/**
 * created by just on 18-5-3
 */
public class ReMapNode extends PlanNode {

    PlanNode source;

    public ReMapNode(PlanNode source) {
        this.source = source;
    }

    @Override
    public Schema getSchema() {
        return null;
    }

    @Override
    public PlanNode getSource() {
        return source;
    }

    @Override
    public MonitorKStreamBuilder buildDSL(StringBuilder builder, Map<String, Object> props) {
        return getSource().buildDSL(builder,props).reMap();
    }
}
