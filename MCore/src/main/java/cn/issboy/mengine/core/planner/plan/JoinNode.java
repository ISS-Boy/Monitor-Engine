package cn.issboy.mengine.core.planner.plan;

import cn.issboy.mengine.core.codegen.MonitorKStreamBuilder;
import org.apache.avro.Schema;

import java.util.Map;

/**
 *
 *
 *
 *
 *
 * created by just on 18-4-9
 */
public class JoinNode extends PlanNode {

    public enum Type{
        SINNER,TINNER,STINNER,OUTER,LEFT

    }

    private final Type type;
    private final PlanNode left;
    private final PlanNode right;
    private final Schema schema;

    public JoinNode(Type type, PlanNode left, PlanNode right) {
        this.type = type;
        this.left = left;
        this.right = right;
        // TODO: 18-4-28 merge schema
        this.schema = left.getSchema();
    }

    @Override
    public Schema getSchema() {
        return schema;
    }

    @Override
    public PlanNode getSource() {
        return null;
    }


    // 递归遍历Join树
    // 如果Join树的左右孩子分别为stream-table,此时做join时需要对stream做remap
    @Override
    public MonitorKStreamBuilder buildDSL(StringBuilder builder, Map<String, Object> props) {
        switch (this.type){
            case SINNER:
                return left.buildDSL(builder,props).streamJoin(right.buildDSL(new StringBuilder(),props));
            case TINNER:
                return left.buildDSL(builder,props).tableJoin(right.buildDSL(new StringBuilder(),props));
            case STINNER:
                return left.buildDSL(builder,props).reMap().streamTableJoin(right.buildDSL(new StringBuilder(),props));
            case OUTER:
                // TODO: 18-4-23 future work
                return null;
            default:
                return null;

        }



    }
}
