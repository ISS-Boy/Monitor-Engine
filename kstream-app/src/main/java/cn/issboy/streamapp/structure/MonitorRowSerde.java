package cn.issboy.streamapp.structure;

import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.serialization.Serializer;

import java.util.Map;

/**
 * created by just on 18-1-26
 */
public class MonitorRowSerde implements Serde<GenericRow> {

    private final Serde<GenericRow> inner;

    public MonitorRowSerde() {
        this.inner = Serdes.serdeFrom(new MonitorGenericRowAvroSerializer(),new MonitorGenericRowAvroDeserializer());

    }

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {
        inner.serializer().configure(configs,isKey);
        inner.deserializer().configure(configs,isKey);
    }

    @Override
    public void close() {
        inner.serializer().close();
        inner.deserializer().close();

    }

    @Override
    public Serializer<GenericRow> serializer() {
        return inner.serializer();
    }

    @Override
    public Deserializer<GenericRow> deserializer() {
        return inner.deserializer();
    }
}
