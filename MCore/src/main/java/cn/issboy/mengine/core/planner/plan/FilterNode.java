package cn.issboy.mengine.core.planner.plan;

import cn.issboy.mengine.core.codegen.MonitorKStreamBuilder;
import org.apache.avro.Schema;

import java.util.List;
import java.util.Map;

/**
 * created by just on 18-1-3
 */
public class FilterNode extends PlanNode {
    private PlanNode source;
    private final List<String> predicates;
    private final List<String> measures;
    private final Schema schema;

    public FilterNode(PlanNode Source, List<String> predicates,List<String> measures) {
        this.source = Source;
        this.predicates = predicates;
        this.schema = Source.getSchema();
        this.measures = measures;
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
        return source;
    }

    public List<String> getMeasures() {
        return measures;
    }

    @Override
    public MonitorKStreamBuilder buildDSL(StringBuilder builder, Map<String, Object> props) {
        return getSource().buildDSL(builder, props).filterMap(getPredicates(),getMeasures());

    }

}
