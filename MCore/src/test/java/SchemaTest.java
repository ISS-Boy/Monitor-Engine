import cn.issboy.mengine.core.metastore.MetaStore;
import cn.issboy.mengine.core.metastore.MetaStoreUtil;
import cn.issboy.mengine.core.metastore.SchemadDataSource;
import org.apache.avro.Schema;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * created by just on 18-4-20
 */

public class SchemaTest {



    @Test
    public void schemaTest() throws IOException {
        String avroFilePath = "/home/just/IdeaProjects/MEngine/MCore/src/test/Resources/avros/bloodPressure.avsc";
        SchemadDataSource schemadDataSource= MetaStoreUtil.loadFromAvroFile(avroFilePath);
        String type = schemadDataSource.getSchema().getField("systolic_blood_pressure").schema().getType().toString();
        assert (type.equals("FLOAT"));
    }


}
