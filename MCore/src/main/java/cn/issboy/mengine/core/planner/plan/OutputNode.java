package cn.issboy.mengine.core.planner.plan;

import cn.issboy.mengine.core.codegen.MonitorKStreamBuilder;
import org.apache.avro.Schema;

import java.util.Map;

/**
 * created by just on 18-1-3
 */
public class OutputNode extends PlanNode{

    PlanNode Source;

    String monitorId;

    public OutputNode(PlanNode Source, String monitorId) {
        this.Source = Source;
        this.monitorId = monitorId;
    }

    @Override
    public Schema getSchema() {
        return null;
    }

    @Override
    public PlanNode getSource() {
        return Source;
    }

    @Override
    public MonitorKStreamBuilder buildDSL(StringBuilder builder, Map<String, Object> props) {
        return getSource().buildDSL(builder,props)
                .into(props.get("monitorGroupId") + monitorId,props.get("topic").toString());
    }
}
