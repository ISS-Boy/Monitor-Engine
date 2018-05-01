package cn.issboy.mengine.core.planner.plan;

import cn.issboy.mengine.core.codegen.MonitorKStreamBuilder;
import org.apache.avro.Schema;

import java.util.List;
import java.util.Map;

/**
 * created by just on 18-1-3
 */
public class ProjectNode extends PlanNode{

    PlanNode Source;
    private final List<String> selectValues;

    public ProjectNode(PlanNode Source, List<String> selectValues) {
        this.Source = Source;
        this.selectValues = selectValues;
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
        return getSource().buildDSL(builder,props).select(selectValues);
    }

}
