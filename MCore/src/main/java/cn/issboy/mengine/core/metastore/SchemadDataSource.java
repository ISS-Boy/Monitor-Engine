package cn.issboy.mengine.core.metastore;


import org.apache.avro.Schema;

/**
 * created by just on 18-4-10
 */
public class SchemadDataSource implements DataSource{

    // synthea(Table)或mhealth(Stream)
    private final DataType dataType;
    // avro schema
    private final Schema schema;

    private final String topicName;

    public SchemadDataSource(DataType dataType, Schema schema, String topicName) {
        this.dataType = dataType;
        this.schema = schema;
        this.topicName = topicName;
    }

    @Override
    public String getTopicName() {
        return topicName;
    }

    public Schema getSchema() {
        return schema;
    }



    @Override
    public DataType getDataType() {
        return dataType;
    }

}
