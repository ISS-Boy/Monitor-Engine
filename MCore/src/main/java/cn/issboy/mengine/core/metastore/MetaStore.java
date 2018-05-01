package cn.issboy.mengine.core.metastore;

/**
 * created by just on 18-3-28
 */
public interface MetaStore {

//    DataSource.DataType getType();

    SchemadDataSource getDataSource(String topicName);

    void putDataSource(SchemadDataSource source);

    MetaStore clone();



}
