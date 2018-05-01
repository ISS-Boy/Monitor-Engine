package cn.issboy.server.controller;

import cn.issboy.mengine.core.metastore.MetaStore;
import cn.issboy.mengine.core.metastore.MetaStoreUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Todo fecth from remote registry, maybe schema update needed
 * created by just on 18-4-29
 */

public class SchemaFetcher implements Runnable{

    private String path;
    private MetaStore metaStore;

    public SchemaFetcher(MetaStore metaStore,String path) {
        this.metaStore = metaStore;
        this.path = path;
    }

    @Override
    public void run() {
        File file = new File(path);

        File[] avroFiles = file.listFiles();
        for(File avroFile : avroFiles){
            metaStore.putDataSource(MetaStoreUtil.loadFromAvroFile(avroFile.getPath()));
        }
    }
}
