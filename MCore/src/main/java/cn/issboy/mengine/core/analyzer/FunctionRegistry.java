package cn.issboy.mengine.core.analyzer;

import org.apache.avro.Schema;

/**
 * function工厂
 * created by just on 18-4-24
 */
public class FunctionRegistry {

    public String getAggResult(String val,String agg,String function){
        StringBuilder sb = new StringBuilder();
        sb.append("agg.getValues().put(");
        switch (function){
            case "min":
                sb.append(val)
                        .append(" < ")
                        .append(agg)
                        .append("?");
                return null;
            case "max":
                return null;
            case "rate":

                return null;

        }
        return null;
    }
    private String castFieldType(String type,String measure,boolean isAgg){
        StringBuilder sb = new StringBuilder();
        sb.append("(")
                .append(type)
                .append(")");
        if(isAgg){
            sb.append("agg.getValues().get(");
        }else{
            sb.append("val.getValues().get)");
        }
        sb.append(measure)
                .append(")");
        return sb.toString();
    }
}
