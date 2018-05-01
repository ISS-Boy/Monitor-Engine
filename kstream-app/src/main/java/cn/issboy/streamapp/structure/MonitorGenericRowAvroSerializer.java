package cn.issboy.streamapp.structure;

import io.confluent.kafka.serializers.KafkaAvroSerializer;
import org.apache.avro.Schema;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericRecord;
import org.apache.kafka.common.serialization.Serializer;

import java.util.HashMap;
import java.util.Map;

/**
 * created by just on 18-1-22
 */
public class MonitorGenericRowAvroSerializer implements Serializer<GenericRow> {

    private final KafkaAvroSerializer kafkaAvroSerializer;
    private final static String MONITOR_AVRO = "{\n" +
            "    \"namespace\": \"org.mhealth.open.data.avro\",\n" +
            "    \"name\": \"Monitor\",\n" +
            "    \"type\": \"record\",\n" +
            "    \"fields\": [\n" +
            "        {\"name\": \"timestamp\", \"type\": \"long\"},\n" +
            "        {\"name\": \"measures\" ,\"type\": {\n" +
            "            \"type\":\"map\",\"values\":{\"type\" : \"float\"}\n" +
            "            }\n" +
            "        }\n" +
            "    ]\n" +
            "}";
    private Schema avroSchema;

    public MonitorGenericRowAvroSerializer() {

        avroSchema = new Schema.Parser().parse(MONITOR_AVRO);
        this.kafkaAvroSerializer = new KafkaAvroSerializer();
    }

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {
        this.kafkaAvroSerializer.configure(configs,isKey);
    }

    @Override
    public byte[] serialize(String topic, GenericRow data) {
        if(data == null || data.getValues()==null){
            return null;
        }
        try{

            GenericRecord genericRecord = new GenericData.Record(avroSchema);
            Map<String,Float> measureMap = new HashMap<>();
            data.getValues().forEach((String key,Object value)->{
                if(key.equals("timestamp")){
                    genericRecord.put(key,value);
                }else if (value instanceof Float){
                    // TODO: 18-2-27 support a fixed schema only, declare a schema variable in this class.
                    // TODO: 18-2-27 and generate an avro schema dynamically.(more topics needed however zzz)
                    measureMap.put(key,(Float) value);
                }
            });
            genericRecord.put("measures",measureMap);
            return kafkaAvroSerializer.serialize(topic,genericRecord);
        }catch (Exception e){
            throw new RuntimeException(e);
        }

    }

    @Override
    public void close() {

    }
}
