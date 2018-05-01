package cn.issboy.streamapp;

import cn.issboy.streamapp.structure.*;
import cn.issboy.streamapp.structure.functions.*;
import io.confluent.kafka.serializers.AbstractKafkaAvroSerDeConfig;
import io.confluent.kafka.streams.serdes.avro.SpecificAvroSerde;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.*;
import org.apache.kafka.streams.processor.TopologyBuilder;
import org.apache.kafka.streams.state.RocksDBConfigSetter;
import org.apache.log4j.Logger;
import org.rocksdb.Options;

import java.io.IOException;
import java.util.*;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

public class Main {

    static final String BLOOD_PRESSURE = "blood-pressure";
    static final String HEART_RATE = "heart-rate";
    static final String BODY_TEMPERATURE = "body-temperature";
    static final String STEP_COUNT = "step-count";
    static final String SLEEP_DURATION = "sleep-duration";
    static final String BODY_FAT_PERCENTAGE = "body-fat-percentage";

    static final FunctionRegistry functionRegistry = FunctionRegistry.getFunctionRegistry();

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

        final String bootstrapServers = "192.168.222.226:9092";
        final String schemaRegistryUrl = "http://192.168.222.226:8081";

        System.out.println("Connecting to Kafka cluster via bootstrap servers " + bootstrapServers);
        System.out.println("Connecting to Confluent schema registry at " + schemaRegistryUrl);

        for(KafkaStreams streams : createStreams(bootstrapServers,
                schemaRegistryUrl,
                "/tmp/streams-example")){
            streams.cleanUp();
            streams.start();

            // shutdown gracefully
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                try {
                    streams.close();
                } catch (Exception e) {
                    // ignored
                }
            }));
        }


    }




    static List<KafkaStreams> createStreams(final String bootstrapServers,
                                            final String schemaRegistryUrl,
                                            final String stateDir) throws IOException {

        final Map<String, String> serdeConfig = Collections.singletonMap(
                AbstractKafkaAvroSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG, schemaRegistryUrl);

        final MonitorRowSerde monitorRowSerde = new MonitorRowSerde();
        monitorRowSerde.configure(serdeConfig, false);


        final Properties properties0 = getStreamConfig("123_123",
                bootstrapServers, schemaRegistryUrl, stateDir);

        final KStreamBuilder builder0 = new KStreamBuilder();

        return Arrays.asList(new KafkaStreams(builder0 (builder0,monitorRowSerde),properties0));
    }

    public static KStreamBuilder builder0 (KStreamBuilder builder, MonitorRowSerde monitorRowSerde) {
        builder.stream(Serdes.String(),monitorRowSerde,HEART_RATE)
                .filter((key, value) -> key.equals("the-user-1"))
                .map((key,value)-> KeyValue.pair(key + value.getValues().get("timestamp").toString(),value))
                .join(builder.stream(Serdes.String(),monitorRowSerde,BLOOD_PRESSURE)
                                .filter((key, value) -> key.equals("the-user-1"))
                                .map((key,value)-> KeyValue.pair(key + value.getValues().get("timestamp").toString(),value))
                        ,(left,right) -> {GenericRow res = new GenericRow(new HashMap<>(left.getValues()));
                            res.getValues().putAll(right.getValues());
                            return res;},
                        JoinWindows.of(TimeUnit.MINUTES.toMillis(10)),Serdes.String(),monitorRowSerde,monitorRowSerde).groupByKey()
                .aggregate(new MInitializer("收缩压次数","count","舒张压次数","count","心率次数","count"),new MAggregator(functionRegistry,"收缩压次数","count","systolic_blood_pressure","systolic_blood_pressure>123","舒张压次数","count","diastolic_blood_pressure","diastolic_blood_pressure>180","心率次数","count","heart_rate","heart_rate>120"),TimeWindows.of(TimeUnit.MINUTES.toMillis(10)),monitorRowSerde)
                .toStream()
                .filter((k,v)->(Float)v.getValues().get("收缩压次数")>5&&(Float)v.getValues().get("舒张压次数")>5&&(Float)v.getValues().get("心率次数")>5)
                .mapValues(new SelectValueMapper("收缩压次数","舒张压次数","心率次数"))
                .map((k,v) -> KeyValue.pair("123_123",v))
                .to(Serdes.String(),monitorRowSerde,"monitor-test2");
        return builder;
    }





    public static Properties getStreamConfig(String appId,
                                             final String bootstrapServers,
                                             final String schemaRegistryUrl,
                                             final String stateDir){
        final Properties properties = new Properties();
        properties.put(StreamsConfig.APPLICATION_ID_CONFIG, appId);
        properties.put(StreamsConfig.CLIENT_ID_CONFIG, "interactive-queries-client");
        properties.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        properties.put(AbstractKafkaAvroSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG, schemaRegistryUrl);
        properties.put(StreamsConfig.STATE_DIR_CONFIG, stateDir);
        properties.put(StreamsConfig.NUM_STREAM_THREADS_CONFIG,5);
        properties.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
        properties.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, SpecificAvroSerde.class);
        properties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");
        properties.put(StreamsConfig.COMMIT_INTERVAL_MS_CONFIG, 10 * 1000);
        properties.put(StreamsConfig.DEFAULT_TIMESTAMP_EXTRACTOR_CLASS_CONFIG, "cn.issboy.streamapp.CustomTimestampExtractor");
        // turn off the record cache so as to get all agg results on every datapoint,
        // change 0 to 200 * 1024 * 1024 to turn on record cache(will do compact ahead).
        properties.put(StreamsConfig.CACHE_MAX_BYTES_BUFFERING_CONFIG, 0);

        // Allow the user to fine-tune the `metadata.max.age.ms` via Java system properties from the CLI.
        // Lowering this parameter from its default of 5 minutes to a few seconds is helpful in
        // situations where the input topic was not pre-created before running the application because
        // the application will discover a newly created topic faster.  In production, you would
        // typically not change this parameter from its default.
        String metadataMaxAgeMs = System.getProperty(ConsumerConfig.METADATA_MAX_AGE_CONFIG);
        if (metadataMaxAgeMs != null) {
            try {
                int value = Integer.parseInt(metadataMaxAgeMs);
                properties.put(ConsumerConfig.METADATA_MAX_AGE_CONFIG, value);
            } catch (NumberFormatException ignored) {

            }
        }
        return properties;
    }



}