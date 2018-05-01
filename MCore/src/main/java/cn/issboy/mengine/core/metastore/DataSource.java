package cn.issboy.mengine.core.metastore;

/**
 * created by just on 18-4-10
 */
public interface DataSource {

    enum DataType{
        STREAM("stream"),TABLE("table");
        private final String metricType;
        DataType(String metricType){
            this.metricType = metricType;
        }
        @Override
        public String toString() {
            return this.metricType;
        }
    }

    String getTopicName();

    DataType getDataType();

}
