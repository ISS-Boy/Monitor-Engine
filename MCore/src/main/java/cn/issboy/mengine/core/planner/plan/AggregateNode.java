package cn.issboy.mengine.core.planner.plan;

import cn.issboy.mengine.core.codegen.MonitorKStreamBuilder;
import org.apache.avro.Schema;

import java.util.Map;

/**
 * created by just on 18-1-24
 */
public class AggregateNode extends PlanNode {


    private PlanNode Source;

    // follow the pattern {alias0,function0,...}
    private String initilizer;

    // follow the pattern {alias0,function0,measure0,predicate0,...}
    private String aggContext;

    private String window;

    public AggregateNode(PlanNode Source, String initilizer, String aggContext, String window) {
        this.Source = Source;
        this.initilizer = initilizer;
        this.aggContext = aggContext;
        this.window = window;
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
        return getSource().buildDSL(builder,props).aggregate(initilizer,aggContext,window);
    }

}

