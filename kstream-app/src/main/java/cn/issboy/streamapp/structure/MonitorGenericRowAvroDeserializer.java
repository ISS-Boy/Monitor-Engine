package cn.issboy.streamapp.structure;

import io.confluent.kafka.serializers.KafkaAvroDeserializer;
import org.apache.avro.Schema;
import org.apache.avro.generic.GenericRecord;
import org.apache.kafka.common.serialization.Deserializer;

import java.util.HashMap;
import java.util.Map;

/**
 * created by just on 18-1-22
 */
public class MonitorGenericRowAvroDeserializer implements Deserializer<GenericRow> {

    private final KafkaAvroDeserializer kafkaAvroDeserializer;

    public MonitorGenericRowAvroDeserializer(){
        this.kafkaAvroDeserializer = new KafkaAvroDeserializer();
    }

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {
        this.kafkaAvroDeserializer.configure(configs,isKey);
    }

    @Override
    public GenericRow deserialize(String topic, byte[] data) {

        if(data == null){
            return null;
        }

        GenericRow genericRow;
        try{
            GenericRecord genericRecord = (GenericRecord) kafkaAvroDeserializer.deserialize(topic,data);

            Map<String,Object> columnsMap = new HashMap<>();

            for(Schema.Field field : genericRecord.getSchema().getFields()){
                Object value = genericRecord.get(field.name());
                switch (field.schema().getType()){
                    case INT:
                    case LONG:
                    case FLOAT:
                    case DOUBLE:
                        columnsMap.put(field.name(),value);
                        break;
                    case UNION:
                    case STRING:
                        if(value != null){
                            columnsMap.put(field.name(),value.toString());
                        }else {
                            columnsMap.put(field.name(),value);
                        }
                        break;
                    case MAP:
                        // TODO: 18-2-26 find a better way to handle nest avro record
                        ((Map)value).forEach((measureName,val)->{
                            if(val instanceof GenericRecord){
                                columnsMap.put(measureName.toString(),((GenericRecord) val).get("value"));
                            }else{
                                // add more types here if needed
                                columnsMap.put(measureName.toString(),val);
                            }
                        });
                        break;
                    default:
                            break;
                }

            }
            genericRow = new GenericRow(columnsMap);

        }catch (Exception e){
            throw new RuntimeException(e);
        }


        return genericRow;
    }




    @Override
    public void close() {

    }
}
