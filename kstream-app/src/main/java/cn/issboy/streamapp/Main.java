package cn.issboy.streamapp;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.serialization.Serializer;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.JoinWindows;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KStreamBuilder;
import org.apache.kafka.streams.state.HostInfo;
import org.apache.kafka.streams.state.RocksDBConfigSetter;
import org.mhealth.open.data.avro.MEvent;
import org.mhealth.open.data.avro.SPatient;
import org.rocksdb.Options;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import io.confluent.kafka.serializers.AbstractKafkaAvroSerDeConfig;
import io.confluent.kafka.streams.serdes.avro.SpecificAvroSerde;

public class Main {



    public static final String STORE_NAME1 = "null";
    public static final String STORE_NAME2 = "null";


    public static class CustomRocksDBConfig implements RocksDBConfigSetter {

        @Override
        public void setConfig(final String storeName, final Options options, final Map<String, Object> configs) {
            // Workaround: We must ensure that the parallelism is set to >= 2.  There seems to be a known
            // issue with RocksDB where explicitly setting the parallelism to 1 causes issues (even though
            // 1 seems to be RocksDB's default for this configuration).
            int compactionParallelism = Math.max(Runtime.getRuntime().availableProcessors(), 2);
            // Set number of compaction threads (but not flush threads).
            options.setIncreaseParallelism(compactionParallelism);
        }
    }

    public static void main(String[] args) throws Exception{

        final int restEndpointPort = 8080;
        final String restEndpointHostname = "localhost";
        final String bootstrapServers = "192.168.222.226:9092";
        final String schemaRegistryUrl = "http://192.168.222.226:8081";

        System.out.println("Connecting to Kafka cluster via bootstrap servers " + bootstrapServers);
        System.out.println("Connecting to Confluent schema registry at " + schemaRegistryUrl);
        System.out.println("REST endpoint at http://" + restEndpointHostname + ":" + restEndpointPort);

        final KafkaStreams streams = createStreams(bootstrapServers,
                schemaRegistryUrl,
                restEndpointPort,
                "/tmp/streams-example");

        streams.cleanUp();

        streams.start();

    }




    static KafkaStreams createStreams(final String bootstrapServers,
                                      final String schemaRegistryUrl,
                                      final int applicationServerPort,
                                      final String stateDir) throws IOException {

        final Properties streamsConfiguration = new Properties();

        streamsConfiguration.put(StreamsConfig.APPLICATION_ID_CONFIG, "application-test");
        streamsConfiguration.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        streamsConfiguration.put(AbstractKafkaAvroSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG, schemaRegistryUrl);
        streamsConfiguration.put(StreamsConfig.APPLICATION_SERVER_CONFIG, "localhost:" + applicationServerPort);
        streamsConfiguration.put(StreamsConfig.STATE_DIR_CONFIG, stateDir);
        streamsConfiguration.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
        streamsConfiguration.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, SpecificAvroSerde.class );
        streamsConfiguration.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        streamsConfiguration.put(StreamsConfig.COMMIT_INTERVAL_MS_CONFIG, 10 * 1000);

        String metadataMaxAgeMs = System.getProperty(ConsumerConfig.METADATA_MAX_AGE_CONFIG);
        if (metadataMaxAgeMs != null) {
            try {
                int value = Integer.parseInt(metadataMaxAgeMs);
                streamsConfiguration.put(ConsumerConfig.METADATA_MAX_AGE_CONFIG, value);
                System.out.println("Set consumer configuration " + ConsumerConfig.METADATA_MAX_AGE_CONFIG +
                        " to " + value);
            } catch (NumberFormatException ignored) {
            }
        }

        final KStreamBuilder builder = new KStreamBuilder();

        final Map<String, String> serdeConfig = Collections.singletonMap(
                AbstractKafkaAvroSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG, schemaRegistryUrl);

        final SpecificAvroSerde<MEvent> heartrateSerde = new SpecificAvroSerde<>();
        heartrateSerde.configure(serdeConfig,false);
        final SpecificAvroSerde<MEvent> bloodpressureSerde = new SpecificAvroSerde<>();
        bloodpressureSerde.configure(serdeConfig,false);
        KStream<String,MEvent> heartrateStream= builder.stream(Serdes.String(),heartrateSerde,"heart-rate").filter((key,value) -> value.getMeasures().get("heart_rate").getValue() >= 120);
        KStream<String,MEvent> bloodpressureStream= builder.stream(Serdes.String(),bloodpressureSerde,"blood-pressure");
        KStream<String,String> joined = heartrateStream.join(bloodpressureStream,(left,right)-> {return "heart-rate = " + left.getMeasures().get("heart_rate").getValue() + right.getMeasures().get("systolic_blood_pressure").getValue();},JoinWindows.of(TimeUnit.MINUTES.toMillis(5)),Serdes.String(),heartrateSerde,bloodpressureSerde);
        joined.to(Serdes.String(),Serdes.String(),"monitorId2018-01-10");

        return new KafkaStreams(builder,streamsConfiguration);
    }
}
//public class Main{
//
//    public static void main(String[] args) {
//
//    }
//
//}