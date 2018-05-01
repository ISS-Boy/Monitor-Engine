package cn.issboy.streamapp.structure;

import java.time.Instant;
import java.util.Map;

/**
 * created by just on 18-1-18
 */
public class GenericRow {

    private Map<String,Object> values;

    public GenericRow(Map<String, Object> values) {
        this.values = values;
    }

    public Map<String, Object> getValues() {
        return values;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        int[] count = {1};
        values.forEach((key,value)->{
            if(key.equals("timestamp")){
                value = Instant.ofEpochMilli((Long)value).toString();
            }
            sb.append(key).append(" : ").append(value);
            if(count[0] != values.size()){
                count[0]++;
                sb.append(" , ");
            }
        });
        return sb.toString();
    }
}
