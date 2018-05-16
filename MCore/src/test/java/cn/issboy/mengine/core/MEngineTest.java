package cn.issboy.mengine.core;

import cn.issboy.mengine.core.metastore.MetaStore;
import cn.issboy.mengine.core.metastore.MetaStoreUtil;
import cn.issboy.mengine.core.parser.BlockGroup;
import com.alibaba.fastjson.JSONObject;
import org.junit.Test;
import sun.tools.jar.Main;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import java.util.zip.ZipEntry;

/**
 * created by just on 18-5-2
 */
public class MEngineTest {
    MEngine mEngine = MEngine.getSingletonEngine();

    @Test
    public void createFolderTest() throws IOException {

        mEngine.createFolder("./test");
        mEngine.createFolder("./test/test");
        File folder = new File("./test");
        File subFolder = new File("./test/test");
        File file = new File("./test/test/test");
        FileOutputStream writer = new FileOutputStream(file);
        writer.write("test".getBytes());
        assert (file.exists());
        folder.deleteOnExit();
        subFolder.deleteOnExit();
        file.deleteOnExit();
        writer.close();
    }

    @Test
    public void updateJarTest() throws IOException {
        long start = Instant.now().toEpochMilli();
        mEngine.createFolder("/home/just/IdeaProjects/MEngine/kstream-app/target/cn/issboy/streamapp/");
        File file = new File("/home/just/IdeaProjects/MEngine/kstream-app/target/cn/issboy/streamapp/testConfig.class");
        FileOutputStream writer = new FileOutputStream(file);
        writer.write("test".getBytes());
        String cmd = "cd /home/just/IdeaProjects/MEngine/kstream-app/target \n"
                + "jar uf kstream-app-template-1.0-SNAPSHOT-jar-with-dependencies.jar ./cn/issboy/streamapp/testConfig.class";
        String[] cmds = {"/bin/sh", "-c", cmd};
        Process process;
        try {
            process = Runtime.getRuntime().exec(cmds);
            process.waitFor();
            InputStream inputStream = process.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
            process.destroy();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(Instant.now().toEpochMilli() - start);
    }


    @Test
    public void copy2Nfs() throws IOException {
        long start = Instant.now().toEpochMilli();
        Path path = Paths.get("/home/just/IdeaProjects/MEngine/kstream-app/target/kstream-app-template-1.0-SNAPSHOT-jar-with-dependencies.jar");
        File file = new File("/mnt/nfs/tst.jar");
        try(FileOutputStream fops = new FileOutputStream(file)){
            Files.copy(path, fops);
        }
        System.out.println(file.exists());
        System.out.println(Instant.now().toEpochMilli() - start);
        file.deleteOnExit();
    }

    @Test
    public void jarOutputStreamTest() throws IOException {
        File tmpJar = new File("/home/just/test.jar");
        JarOutputStream target = new JarOutputStream(new FileOutputStream(tmpJar));
        ZipEntry entry = new ZipEntry("cn/issboy/streamapp/SunJarTool.class");
        JarEntry entry2 = new JarEntry("cn/issboy/streamapp/AAA.class");
        target.putNextEntry(entry);
        byte[] buf = {'m', 'a', 'i', 'n'};
        target.write(buf);
        target.closeEntry();
        target.putNextEntry(entry2);
        target.write(buf);
        target.closeEntry();
        target.close();
    }

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
                        "\t\t\t\"s_meaOrCal\": \"1\"\n" +
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

    String complex_json = "{\n" +
                          "\t\t\"blockGroupName\": \"经典咖啡时光\",\n" +
                          "\t\t\"blockValues\": [{\n" +
                          "\t\t\t\"aggregation\": {\n" +
                          "\t\t\t\t\"aggregationValues\": [{\n" +
                          "\t\t\t\t\t\"measure\": \"systolic_blood_pressure\",\n" +
                          "\t\t\t\t\t\"name\": \"收缩压次数\",\n" +
                          "\t\t\t\t\t\"predicates\": [{\n" +
                          "\t\t\t\t\t\t\"boolExp\": \"and\",\n" +
                          "\t\t\t\t\t\t\"measure\": \"systolic_blood_pressure\",\n" +
                          "\t\t\t\t\t\t\"op\": \">\",\n" +
                          "\t\t\t\t\t\t\"source\": \"blood-pressure\",\n" +
                          "\t\t\t\t\t\t\"threshold\": \"110\"\n" +
                          "\t\t\t\t\t}],\n" +
                          "\t\t\t\t\t\"source\": \"blood-pressure\",\n" +
                          "\t\t\t\t\t\"type\": \"count\"\n" +
                          "\t\t\t\t}, {\n" +
                          "\t\t\t\t\t\"measure\": \"heart_rate\",\n" +
                          "\t\t\t\t\t\"name\": \"心率次数\",\n" +
                          "\t\t\t\t\t\"predicates\": [{\n" +
                          "\t\t\t\t\t\t\"boolExp\": \"and\",\n" +
                          "\t\t\t\t\t\t\"measure\": \"heart_rate\",\n" +
                          "\t\t\t\t\t\t\"op\": \">\",\n" +
                          "\t\t\t\t\t\t\"source\": \"heart-rate\",\n" +
                          "\t\t\t\t\t\t\"threshold\": \"70\"\n" +
                          "\t\t\t\t\t}],\n" +
                          "\t\t\t\t\t\"source\": \"heart-rate\",\n" +
                          "\t\t\t\t\t\"type\": \"count\"\n" +
                          "\t\t\t\t}, {\n" +
                          "\t\t\t\t\t\"measure\": \"body_temperature\",\n" +
                          "\t\t\t\t\t\"name\": \"体温次数\",\n" +
                          "\t\t\t\t\t\"predicates\": [{\n" +
                          "\t\t\t\t\t\t\"boolExp\": \"and\",\n" +
                          "\t\t\t\t\t\t\"measure\": \"body_temperature\",\n" +
                          "\t\t\t\t\t\t\"op\": \"<\",\n" +
                          "\t\t\t\t\t\t\"source\": \"body-temperature\",\n" +
                          "\t\t\t\t\t\t\"threshold\": \"38\"\n" +
                          "\t\t\t\t\t}],\n" +
                          "\t\t\t\t\t\"source\": \"body-temperature\",\n" +
                          "\t\t\t\t\t\"type\": \"count\"\n" +
                          "\t\t\t\t}],\n" +
                          "\t\t\t\t\"window\": {\n" +
                          "\t\t\t\t\t\"windowInterval\": \"\",\n" +
                          "\t\t\t\t\t\"windowLength\": \"10\",\n" +
                          "\t\t\t\t\t\"windowType\": \"tumbling-window\"\n" +
                          "\t\t\t\t}\n" +
                          "\t\t\t},\n" +
                          "\t\t\t\"filters\": [{\n" +
                          "\t\t\t\t\"f_boolExp\": \"and\",\n" +
                          "\t\t\t\t\"f_measure\": \"收缩压次数\",\n" +
                          "\t\t\t\t\"f_op\": \">\",\n" +
                          "\t\t\t\t\"f_source\": \"blood-pressure\",\n" +
                          "\t\t\t\t\"f_threshold\": \"2\"\n" +
                          "\t\t\t}, {\n" +
                          "\t\t\t\t\"f_boolExp\": \"and\",\n" +
                          "\t\t\t\t\"f_measure\": \"心率次数\",\n" +
                          "\t\t\t\t\"f_op\": \">\",\n" +
                          "\t\t\t\t\"f_source\": \"heart-rate\",\n" +
                          "\t\t\t\t\"f_threshold\": \"2\"\n" +
                          "\t\t\t}, {\n" +
                          "\t\t\t\t\"f_boolExp\": \"and\",\n" +
                          "\t\t\t\t\"f_measure\": \"体温次数\",\n" +
                          "\t\t\t\t\"f_op\": \">\",\n" +
                          "\t\t\t\t\"f_source\": \"body-temperature\",\n" +
                          "\t\t\t\t\"f_threshold\": \"2\"\n" +
                          "\t\t\t}],\n" +
                          "\t\t\t\"monitorName\": \"查询1\",\n" +
                          "\t\t\t\"selects\": [{\n" +
                          "\t\t\t\t\"s_meaOrCal\": \"收缩压次数\",\n" +
                          "\t\t\t\t\"s_source\": \"blood-pressure\"\n" +
                          "\t\t\t}, {\n" +
                          "\t\t\t\t\"s_meaOrCal\": \"心率次数\",\n" +
                          "\t\t\t\t\"s_source\": \"heart-rate\"\n" +
                          "\t\t\t}, {\n" +
                          "\t\t\t\t\"s_meaOrCal\": \"体温次数\",\n" +
                          "\t\t\t\t\"s_source\": \"body-temperature\"\n" +
                          "\t\t\t}],\n" +
                          "\t\t\t\"source\": [{\n" +
                          "\t\t\t\t\"sourceName\": \"blood-pressure\"\n" +
                          "\t\t\t}, {\n" +
                          "\t\t\t\t\"sourceName\": \"heart-rate\"\n" +
                          "\t\t\t}, {\n" +
                          "\t\t\t\t\"sourceName\": \"body-temperature\"\n" +
                          "\t\t\t}]\n" +
                          "\t\t}, {\n" +
                          "\t\t\t\"aggregation\": {\n" +
                          "\t\t\t\t\"aggregationValues\": [{\n" +
                          "\t\t\t\t\t\"measure\": \"heart_rate\",\n" +
                          "\t\t\t\t\t\"name\": \"心率增比\",\n" +
                          "\t\t\t\t\t\"predicates\": [],\n" +
                          "\t\t\t\t\t\"source\": \"heart-rate\",\n" +
                          "\t\t\t\t\t\"type\": \"growth radio\"\n" +
                          "\t\t\t\t}],\n" +
                          "\t\t\t\t\"window\": {\n" +
                          "\t\t\t\t\t\"windowInterval\": \"\",\n" +
                          "\t\t\t\t\t\"windowLength\": \"10\",\n" +
                          "\t\t\t\t\t\"windowType\": \"tumbling-window\"\n" +
                          "\t\t\t\t}\n" +
                          "\t\t\t},\n" +
                          "\t\t\t\"filters\": [{\n" +
                          "\t\t\t\t\"f_boolExp\": \"and\",\n" +
                          "\t\t\t\t\"f_measure\": \"心率增比\",\n" +
                          "\t\t\t\t\"f_op\": \">\",\n" +
                          "\t\t\t\t\"f_source\": \"heart-rate\",\n" +
                          "\t\t\t\t\"f_threshold\": \"4\"\n" +
                          "\t\t\t}],\n" +
                          "\t\t\t\"monitorName\": \"查询2\",\n" +
                          "\t\t\t\"selects\": [{\n" +
                          "\t\t\t\t\"s_meaOrCal\": \"心率增比\",\n" +
                          "\t\t\t\t\"s_source\": \"heart-rate\"\n" +
                          "\t\t\t}],\n" +
                          "\t\t\t\"source\": [{\n" +
                          "\t\t\t\t\"sourceName\": \"heart-rate\"\n" +
                          "\t\t\t}]\n" +
                          "\t\t}]\n" +
                          "\t}";

    @Test
    public void buildMetadataTest() throws Exception {
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
        properties.put("monitorGroupId","asdjfjneqznl0fxrkl");
        properties.put("nfsPath","/mnt/nfs/");
        properties.put("jarPath","/home/just/IdeaProjects/MEngine/kstream-app/target/kstream-app-template-1.0-SNAPSHOT-jar-with-dependencies.jar");
        properties.put("bootstrapServers","192.168.222.226:9092");
        properties.put("schemaRegistry","http://192.168.222.226:8081");
        mEngine.buildMetadata(properties, JSONObject.parseObject(block_join, BlockGroup.class));
        mEngine.buildMetadata(properties, JSONObject.parseObject(block_grouped, BlockGroup.class));
        mEngine.buildMetadata(properties, JSONObject.parseObject(blockJson_filter, BlockGroup.class));

    }


}
