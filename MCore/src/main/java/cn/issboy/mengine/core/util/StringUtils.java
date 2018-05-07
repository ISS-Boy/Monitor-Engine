package cn.issboy.mengine.core.util;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Locale;
import java.util.regex.Pattern;

/**
 * created by just on 18-1-8
 */
public class StringUtils {

    public static String wrapString(String str) {
        StringBuilder sb = new StringBuilder();
        sb.append("\"")
                .append(str)
                .append("\"");

        return sb.toString();
    }

    public static String trimLastSymbol(String str) {
        int length = str.length();
        switch (str.charAt(length - 1)) {
            case ',':
                return str.substring(0, str.length() - 1);
            case '|':
            case '&':
                return str.substring(0, length - 2);
            default:
                return str;
        }

    }

    public static String lowerCase(String type) {
        char[] res = type.toCharArray();
        for (int i = 1; i < res.length; i++) {
            res[i] += ('a' - 'A');
        }
        return new String(res);
    }

    public static String toSymbol(String boolExp) {
        switch (boolExp.toLowerCase()) {
            case "and":
                return "&&";
            case "or":
                return "||";
        }
        return boolExp;
    }

    // format blood-pressure -> BLOOD_PRESSURE
    public static String formatVariable(String topicName) {
        return topicName.replace('-', '_').toUpperCase(Locale.ROOT);
    }

    public static String replaceSeparator(String dir) {
        String separator = System.getProperty("file.separator");
        return dir.replace("/",separator);
    }

    public static String toString(Throwable e){
        StringWriter sw = new StringWriter();
        PrintWriter p = new PrintWriter(sw);
        p.print(e.getClass().getName());
        if(e.getMessage() != null){
            p.print(": " + e.getMessage());
        }
        p.println();
        try{
            e.printStackTrace(p);
            return sw.toString();
        }finally {
            p.close();
        }
    }

    // Todo: just for test. removed after changing the way to implement schemaCatalog
    public static String replaceDash(String topicName) {
        return topicName.replace('_', '-');
    }

}
