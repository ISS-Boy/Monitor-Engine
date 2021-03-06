package cn.issboy.mengine.core.metastore;

import cn.issboy.mengine.core.util.StringUtils;
import org.apache.avro.Schema;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * created by just on 18-4-10
 */
public class MetaStoreUtil {

    public static SchemadDataSource loadFromAvroFile(String avroFilePath){
        try{
            String avro = new String(Files.readAllBytes(Paths.get(avroFilePath)));
            Schema schema = new Schema.Parser().parse(avro);
            return new SchemadDataSource(DataSource.DataType.STREAM,schema, StringUtils.replaceDash(schema.getName()));

        }catch (IOException e){
            throw new RuntimeException("Failed to parse avro for"+ avroFilePath);
        }


    }


}


