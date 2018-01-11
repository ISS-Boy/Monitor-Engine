package cn.issboy.mengine.core.util;

import cn.issboy.mengine.parser.pojo.KafkaConnectionType;
import cn.issboy.mengine.parser.pojo.Monitor;

/**
 * created by just on 18-1-8
 */
public class MonitorMetadata {
    private final String DSLCode;
    private final KafkaConnectionType streamConfig;
//    private final ;

    public MonitorMetadata(String dslCode, KafkaConnectionType streamConfig) {
        this.DSLCode = dslCode;
        this.streamConfig = streamConfig;
    }


    public String getDSLCode() {
        return this.DSLCode;
    }

    public KafkaConnectionType getStreamConfig() {
        return streamConfig;
    }


}
