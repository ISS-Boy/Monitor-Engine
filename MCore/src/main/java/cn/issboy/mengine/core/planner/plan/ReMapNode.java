package cn.issboy.mengine.core.planner.plan;

import cn.issboy.mengine.core.codegen.MonitorKStreamBuilder;
import org.apache.avro.Schema;

import java.util.Map;

/**
 * 在join时为了把stream-stream按时间对齐,预先做了一次map(user_id -> user_id + timestamp)
 * 所以在后续操作时为了保证同一个用户的key一致,需要remap(user_id + timestamp -> user_id)
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
