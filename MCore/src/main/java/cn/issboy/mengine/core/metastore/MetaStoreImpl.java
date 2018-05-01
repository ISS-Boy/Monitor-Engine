package cn.issboy.mengine.core.metastore;

import java.util.HashMap;
import java.util.Map;

/**
 * created by just on 18-4-10
 */
public class MetaStoreImpl implements MetaStore{

    // 作为schemaCatalog,应该从schemaregistry中拿并解析
    Map<String,SchemadDataSource> sourceMap;

    public MetaStoreImpl(){
        sourceMap = new HashMap<>();
    }


    public MetaStoreImpl(Map<String, SchemadDataSource> sourceMap) {
        this.sourceMap = sourceMap;
    }

    @Override
    public SchemadDataSource getDataSource(String topicName) {
        return sourceMap.get(topicName);
    }

    @Override
    public void putDataSource(SchemadDataSource source) {
        sourceMap.put(source.getTopicName(),source);
    }

    @Override
    public MetaStore clone() {

        Map<String,SchemadDataSource> newSourceMap = new HashMap<>();
        newSourceMap.putAll(this.sourceMap);

        return new MetaStoreImpl(newSourceMap);

    }
}
