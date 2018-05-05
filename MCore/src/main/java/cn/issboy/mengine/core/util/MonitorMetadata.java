package cn.issboy.mengine.core.util;


import java.util.ArrayList;
import java.util.List;

/**
 * used by Velocity template resolver
 * created by just on 18-1-8
 */
public class MonitorMetadata {
    private String schemaRegistry;
    private String bootstrapServers;

    private final List<String> DSLCode = new ArrayList<>();
    private final List<String> monitorId = new ArrayList<>();


    public String getSchemaRegistry() {
        return schemaRegistry;
    }

    public void setSchemaRegistry(String schemaRegistry) {
        this.schemaRegistry = schemaRegistry;
    }

    public String getBootstrapServers() {
        return bootstrapServers;
    }

    public void setBootstrapServers(String bootstrapServers) {
        this.bootstrapServers = bootstrapServers;
    }

    public void addDSLCode(String dslCode) {
        DSLCode.add(dslCode);
    }

    public List<String> getDSLCode() {
        return this.DSLCode;
    }

    public void addMonitorId(String id) {
        monitorId.add(id);
    }

    public List<String> getMonitorId() {
        return this.monitorId;
    }


}
