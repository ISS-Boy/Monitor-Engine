import cn.issboy.mengine.core.MEngine;
import cn.issboy.mengine.core.metastore.MetaStore;
import cn.issboy.mengine.core.metastore.MetaStoreUtil;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * created by just on 18-4-29
 */
public class AnalyzerTest {
    String path = "/home/just/IdeaProjects/MEngine/Server/src/main/resources/avros";
    String blockJson_filter = "{\n" +
            "\t\"blockGroupName\": \"随便查询\",\n" +
            "\t\"blockValues\": [{\n" +
            "\t\t\"monitorName\": \"随便表\",\n" +
            "\t\t\"source\": [{\n" +
            "\t\t\t\"sourceName\": \"heart-rate\"\n" +
            "\t\t}],\n" +
            "\t\t\"aggregation\": {\n" +
            "\t\t\t\"window\": {\n" +
            "\t\t\t\t\"windowType\": \"sliding window\",\n" +
            "\t\t\t\t\"windowInterval\": \"123\",\n" +
            "\t\t\t\t\"windowLength\": \"123\"\n" +
            "\t\t\t},\n" +
            "\t\t\t\"aggregationValues\": [{\n" +
            "\t\t\t\t\"name\": \"过滤\",\n" +
            "\t\t\t\t\"source\": \"heart-rate\",\n" +
            "\t\t\t\t\"measure\": \"heart_rate\",\n" +
            "\t\t\t\t\"type\": \"min\",\n" +
            "\t\t\t\t\"predicates\": [{\n" +
            "\t\t\t\t\t\"source\": \"heart-rate\",\n" +
            "\t\t\t\t\t\"measure\": \"heart_rate\",\n" +
            "\t\t\t\t\t\"op\": \">\",\n" +
            "\t\t\t\t\t\"threshold\": \"123\",\n" +
            "\t\t\t\t\t\"boolExp\": \"And\"\n" +
            "\t\t\t\t}]\n" +
            "\t\t\t}]\n" +
            "\t\t},\n" +
            "\t\t\"filters\": [{\n" +
            "\t\t\t\"f_source\": \"heart-rate\",\n" +
            "\t\t\t\"f_measure\": \"heart_rate\",\n" +
            "\t\t\t\"f_op\": \">\",\n" +
            "\t\t\t\"f_threshold\": \"90\",\n" +
            "\t\t\t\"f_boolExp\": \"And\"\n" +
            "\t\t}],\n" +
            "\t\t\"selects\": [{\n" +
            "\t\t\t\"s_source\": \"heart-rate\",\n" +
            "\t\t\t\"s_meaOrCal\": \"过滤\"\n" +
            "\t\t}]\n" +
            "\t}]\n" +
            "}";
    String block_join = "{\n" +
            "\t\"blockGroupName\": \"123\",\n" +
            "\t\"blockValues\": [{\n" +
            "\t\t\"monitorName\": \"123\",\n" +
            "\t\t\"source\": [{\n" +
            "\t\t\t\"sourceName\": \"blood-pressure\"\n" +
            "\t\t}, {\n" +
            "\t\t\t\"sourceName\": \"heart-rate\"\n" +
            "\t\t}],\n" +
            "\t\t\"aggregation\": {\n" +
            "\t\t\t\"window\": {\n" +
            "\t\t\t\t\"windowType\": \"sliding-window\",\n" +
            "\t\t\t\t\"windowInterval\": \"\",\n" +
            "\t\t\t\t\"windowLength\": \"10\"\n" +
            "\t\t\t},\n" +
            "\t\t\t\"aggregationValues\": [{\n" +
            "\t\t\t\t\"name\": \"收缩压次数\",\n" +
            "\t\t\t\t\"source\": \"blood-pressure\",\n" +
            "\t\t\t\t\"measure\": \"systolic_blood_pressure\",\n" +
            "\t\t\t\t\"type\": \"count\",\n" +
            "\t\t\t\t\"predicates\": [{\n" +
            "\t\t\t\t\t\"source\": \"blood-pressure\",\n" +
            "\t\t\t\t\t\"measure\": \"systolic_blood_pressure\",\n" +
            "\t\t\t\t\t\"op\": \">\",\n" +
            "\t\t\t\t\t\"threshold\": \"123\",\n" +
            "\t\t\t\t\t\"boolExp\": \"and\"\n" +
            "\t\t\t\t}]\n" +
            "\t\t\t}, {\n" +
            "\t\t\t\t\"name\": \"舒张压次数\",\n" +
            "\t\t\t\t\"source\": \"blood-pressure\",\n" +
            "\t\t\t\t\"measure\": \"diastolic_blood_pressure\",\n" +
            "\t\t\t\t\"type\": \"count\",\n" +
            "\t\t\t\t\"predicates\": [{\n" +
            "\t\t\t\t\t\"source\": \"blood-pressure\",\n" +
            "\t\t\t\t\t\"measure\": \"diastolic_blood_pressure\",\n" +
            "\t\t\t\t\t\"op\": \">\",\n" +
            "\t\t\t\t\t\"threshold\": \"180\",\n" +
            "\t\t\t\t\t\"boolExp\": \"and\"\n" +
            "\t\t\t\t}]\n" +
            "\t\t\t}, {\n" +
            "\t\t\t\t\"name\": \"心率次数\",\n" +
            "\t\t\t\t\"source\": \"heart-rate\",\n" +
            "\t\t\t\t\"measure\": \"heart_rate\",\n" +
            "\t\t\t\t\"type\": \"count\",\n" +
            "\t\t\t\t\"predicates\": [{\n" +
            "\t\t\t\t\t\"source\": \"heart-rate\",\n" +
            "\t\t\t\t\t\"measure\": \"heart_rate\",\n" +
            "\t\t\t\t\t\"op\": \">\",\n" +
            "\t\t\t\t\t\"threshold\": \"120\",\n" +
            "\t\t\t\t\t\"boolExp\": \"and\"\n" +
            "\t\t\t\t}]\n" +
            "\t\t\t}]\n" +
            "\t\t},\n" +
            "\t\t\"filters\": [{\n" +
            "\t\t\t\"f_source\": \"blood-pressure\",\n" +
            "\t\t\t\"f_measure\": \"收缩压次数\",\n" +
            "\t\t\t\"f_op\": \">\",\n" +
            "\t\t\t\"f_threshold\": \"5\",\n" +
            "\t\t\t\"f_boolExp\": \"and\"\n" +
            "\t\t}, {\n" +
            "\t\t\t\"f_source\": \"blood-pressure\",\n" +
            "\t\t\t\"f_measure\": \"舒张压次数\",\n" +
            "\t\t\t\"f_op\": \">\",\n" +
            "\t\t\t\"f_threshold\": \"5\",\n" +
            "\t\t\t\"f_boolExp\": \"and\"\n" +
            "\t\t}, {\n" +
            "\t\t\t\"f_source\": \"heart-rate\",\n" +
            "\t\t\t\"f_measure\": \"心率次数\",\n" +
            "\t\t\t\"f_op\": \">\",\n" +
            "\t\t\t\"f_threshold\": \"5\",\n" +
            "\t\t\t\"f_boolExp\": \"and\"\n" +
            "\t\t}],\n" +
            "\t\t\"selects\": [{\n" +
            "\t\t\t\"s_source\": \"blood-pressure\",\n" +
            "\t\t\t\"s_meaOrCal\": \"收缩压次数\"\n" +
            "\t\t}, {\n" +
            "\t\t\t\"s_source\": \"blood-pressure\",\n" +
            "\t\t\t\"s_meaOrCal\": \"舒张压次数\"\n" +
            "\t\t}, {\n" +
            "\t\t\t\"s_source\": \"heart-rate\",\n" +
            "\t\t\t\"s_meaOrCal\": \"心率次数\"\n" +
            "\t\t}]\n" +
            "\t}]\n" +
            "}";
    String block_grouped = "{\n" +
            "\t\"blockGroupName\": \"查询计划2\",\n" +
            "\t\"blockValues\": [{\n" +
            "\t\t\"monitorName\": \"查询1\",\n" +
            "\t\t\"source\": [{\n" +
            "\t\t\t\"sourceName\": \"body-fat-percentage\"\n" +
            "\t\t}],\n" +
            "\t\t\"aggregation\": {\n" +
            "\t\t\t\"window\": {\n" +
            "\t\t\t\t\"windowType\": \"sliding-window\",\n" +
            "\t\t\t\t\"windowInterval\": \"\",\n" +
            "\t\t\t\t\"windowLength\": \"\"\n" +
            "\t\t\t},\n" +
            "\t\t\t\"aggregationValues\": []\n" +
            "\t\t},\n" +
            "\t\t\"filters\": [{\n" +
            "\t\t\t\"f_source\": \"body-fat-percentage\",\n" +
            "\t\t\t\"f_measure\": \"body_fat_percentage\",\n" +
            "\t\t\t\"f_op\": \">\",\n" +
            "\t\t\t\"f_threshold\": \"123\",\n" +
            "\t\t\t\"f_boolExp\": \"and\"\n" +
            "\t\t}],\n" +
            "\t\t\"selects\": [{\n" +
            "\t\t\t\"s_source\": \"body-fat-percentage\",\n" +
            "\t\t\t\"s_meaOrCal\": \"体脂.\"\n" +
            "\t\t}]\n" +
            "\t}, {\n" +
            "\t\t\"monitorName\": \"查询2\",\n" +
            "\t\t\"source\": [{\n" +
            "\t\t\t\"sourceName\": \"body-temperature\"\n" +
            "\t\t}],\n" +
            "\t\t\"aggregation\": {\n" +
            "\t\t\t\"window\": {\n" +
            "\t\t\t\t\"windowType\": \"sliding-window\",\n" +
            "\t\t\t\t\"windowInterval\": \"\",\n" +
            "\t\t\t\t\"windowLength\": \"\"\n" +
            "\t\t\t},\n" +
            "\t\t\t\"aggregationValues\": []\n" +
            "\t\t},\n" +
            "\t\t\"filters\": [{\n" +
            "\t\t\t\"f_source\": \"body-temperature\",\n" +
            "\t\t\t\"f_measure\": \"body_temperature\",\n" +
            "\t\t\t\"f_op\": \">\",\n" +
            "\t\t\t\"f_threshold\": \"123\",\n" +
            "\t\t\t\"f_boolExp\": \"and\"\n" +
            "\t\t}],\n" +
            "\t\t\"selects\": [{\n" +
            "\t\t\t\"s_source\": \"body-temperature\",\n" +
            "\t\t\t\"s_meaOrCal\": \"体温.\"\n" +
            "\t\t}]\n" +
            "\t}, {\n" +
            "\t\t\"monitorName\": \"查询3\",\n" +
            "\t\t\"source\": [{\n" +
            "\t\t\t\"sourceName\": \"heart-rate\"\n" +
            "\t\t}],\n" +
            "\t\t\"aggregation\": {\n" +
            "\t\t\t\"window\": {\n" +
            "\t\t\t\t\"windowType\": \"sliding-window\",\n" +
            "\t\t\t\t\"windowInterval\": \"\",\n" +
            "\t\t\t\t\"windowLength\": \"10\"\n" +
            "\t\t\t},\n" +
            "\t\t\t\"aggregationValues\": [{\n" +
            "\t\t\t\t\"name\": \"心率平均\",\n" +
            "\t\t\t\t\"source\": \"heart-rate\",\n" +
            "\t\t\t\t\"measure\": \"heart_rate\",\n" +
            "\t\t\t\t\"type\": \"average\",\n" +
            "\t\t\t\t\"predicates\": []\n" +
            "\t\t\t}]\n" +
            "\t\t},\n" +
            "\t\t\"filters\": [{\n" +
            "\t\t\t\"f_source\": \"heart-rate\",\n" +
            "\t\t\t\"f_measure\": \"heart_rate\",\n" +
            "\t\t\t\"f_op\": \">\",\n" +
            "\t\t\t\"f_threshold\": \"100\",\n" +
            "\t\t\t\"f_boolExp\": \"and\"\n" +
            "\t\t}],\n" +
            "\t\t\"selects\": [{\n" +
            "\t\t\t\"s_source\": \"heart-rate\",\n" +
            "\t\t\t\"s_meaOrCal\": \"心率.\"\n" +
            "\t\t}]\n" +
            "\t}]\n" +
            "}";

    @Test
    public void analyzerTest() throws Exception {
        MEngine mEngine = MEngine.getSingletonEngine();
        File file = new File(path);
        MetaStore metaStore = mEngine.getMetaStore();
        File[] avroFiles = file.listFiles();
        for(File avroFile : avroFiles){
            metaStore.putDataSource(MetaStoreUtil.loadFromAvroFile(avroFile.getPath()));
        }

        Map<String,Object> properties = new HashMap<>();
        properties.put("topic","monitor-test2");
        properties.put("userId","the-user-1");
        properties.put("monitorId","asdjfjneqznl0fxrkl");
        properties.put("nfsPath","/mnt/nfs/");
        properties.put("bootstrapServers","192.168.222.226:9092");
        properties.put("schemaRegistry","http://192.168.222.226:8081");
//        mEngine.buildJar(properties,block_join);
    }
}
