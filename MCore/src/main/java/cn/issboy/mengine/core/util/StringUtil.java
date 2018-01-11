package cn.issboy.mengine.core.util;

/**
 * created by just on 18-1-8
 */
public class StringUtil {

    public static boolean compare(String topic,String measure){
        return trimDash(topic).equals(trimDash(measure));
    }
    public static String trimDash(String dashedString){
        return dashedString.replace("-","").replace("_","");
    }
}
