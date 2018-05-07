package cn.issboy.mengine.core.planner.plan;

import cn.issboy.mengine.core.codegen.MonitorKStreamBuilder;
import cn.issboy.mengine.core.metastore.DataSource;
import cn.issboy.mengine.core.metastore.SchemadDataSource;
import cn.issboy.mengine.core.util.StringUtils;
import org.apache.avro.Schema;

import java.util.Map;

/**
 * created by just on 18-1-24
 */
public class SourceNode extends PlanNode {

    private final SchemadDataSource schemadDataSource;


    public SourceNode(SchemadDataSource schemadDataSource) {
        this.schemadDataSource = schemadDataSource;
    }

    @Override
    public Schema getSchema() {
        return schemadDataSource.getSchema();
    }

    @Override
    public PlanNode getSource() {
        return null;
    }


    /* builder.stream(Serdes.String(), monitorRowSerde, HEART_RATE)
     *           .filter((key, value) -> key.equals("the-user-1"))
     *       .map((key, value) -> KeyValue.pair(key + value.getValues().get("timestamp").toString(), value));
     */
    @Override
    public MonitorKStreamBuilder buildDSL(StringBuilder builder,final Map<String,Object> props) {
        String userId = (String)props.get("userId");

        DataSource.DataType dataType = schemadDataSource.getDataType();
        switch (dataType){
            case STREAM:
                builder.append("builder.stream(")
                        .append("Serdes.String(),monitorRowSerde,")
                        .append(StringUtils.formatVariable(schemadDataSource.getTopicName()))
                        .append(")\n")
                        .append(".filter((key, value) -> key.equals(")
                        .append(StringUtils.wrapString(userId))
                        .append("))\n")
                        .append(".map((key,value)-> KeyValue.pair(key + value.getValues().get(\"timestamp\").toString(),value))\n");
                break;
            case TABLE:
                builder.append("builder.table(TopologyBuilder.AutoOffsetReset.EARLIEST,")
                        .append("Serdes.String(),monitorRowSerde,")
                        .append(StringUtils.formatVariable(schemadDataSource.getTopicName()))
                        .append(")\n")
                        .append(".filter((key, value) -> key.equals(")
                        .append(StringUtils.wrapString(userId))
                        .append("))\n");
                break;

        }


        return new MonitorKStreamBuilder(builder);
    }

}
