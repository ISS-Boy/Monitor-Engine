package cn.issboy.streamapp;

import cn.issboy.streamapp.structure.GenericRow;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.streams.processor.TimestampExtractor;

/**
 * created by just on 18-3-12
 */
public class CustomTimestampExtractor implements TimestampExtractor {
    @Override
    public long extract(ConsumerRecord<Object, Object> record, long previousTimestamp) {


       long timestamp = (Long) ((GenericRow)record.value()).getValues().get("timestamp");


        return timestamp;
    }
}
