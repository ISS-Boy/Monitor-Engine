package cn.issboy.mengine.core.planner.plan;

import cn.issboy.mengine.core.codegen.MonitorKStreamBuilder;
import cn.issboy.mengine.core.metastore.SchemadDataSource;
import org.apache.avro.Schema;

import java.util.Map;

/**
 * created by just on 18-1-8
 */
public abstract class PlanNode {



    public abstract Schema getSchema();

    public abstract PlanNode getSource();

    // dfs
    public abstract MonitorKStreamBuilder buildDSL(final StringBuilder builder,
                                                   final Map<String,Object> props
                                                   );






}
