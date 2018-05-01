package cn.issboy.mengine.core.util;

import org.apache.avro.Schema;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * created by just on 18-4-30
 */
public class SchemaUtil {

    public static Schema expandSchema(Schema rawSchema,String field,Schema.Type type){
        List<Schema.Field> fields = new ArrayList<>();
        for(Schema.Field oldField : rawSchema.getFields()){
            Schema.Field fieldCopy = new Schema.Field(oldField.name(),oldField.schema(),oldField.doc(),oldField.defaultVal());
            fields.add(fieldCopy);
        }
        fields.add(new Schema.Field(field,Schema.create(type),"derived",null));
        return Schema.createRecord(fields);

    }
}
