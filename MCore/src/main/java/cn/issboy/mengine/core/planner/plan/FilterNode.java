package cn.issboy.mengine.core.planner.plan;

import cn.issboy.mengine.core.codegen.MonitorKStreamBuilder;
import org.apache.avro.Schema;

import java.util.List;
import java.util.Map;

/**
 * created by just on 18-1-3
 */
public class FilterNode extends PlanNode {
    private PlanNode Source;
    private final List<String> predicates;
    private final Schema schema;
    private final boolean alert;

    public FilterNode(PlanNode Source, List<String> predicates, boolean alert) {
        this.Source = Source;
        this.predicates = predicates;
        this.schema = Source.getSchema();
        this.alert = alert;
    }

    public List<String> getPredicates() {
        return predicates;
    }

    @Override
    public Schema getSchema() {
        return null;
    }

    @Override
    public PlanNode getSource() {
        return Source;
    }

    public boolean isAlert() {
        return alert;
    }

    @Override
    public MonitorKStreamBuilder buildDSL(StringBuilder builder, Map<String, Object> props) {
        return isAlert() ? getSource().buildDSL(builder, props).filterMap(getPredicates())
                : getSource().buildDSL(builder, props).filter(getPredicates());
    }

}
