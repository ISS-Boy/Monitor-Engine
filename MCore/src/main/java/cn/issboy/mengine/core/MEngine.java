package cn.issboy.mengine.core;

import cn.issboy.mengine.core.analyzer.Analysis;
import cn.issboy.mengine.core.analyzer.MonitorAnalyzer;
import cn.issboy.mengine.core.codegen.MonitorKStreamBuilder;
import cn.issboy.mengine.core.codegen.StringCompiler;
import cn.issboy.mengine.core.codegen.TemplateResolver;
import cn.issboy.mengine.core.metastore.MetaStore;
import cn.issboy.mengine.core.metastore.MetaStoreImpl;
import cn.issboy.mengine.core.parser.BlockGroup;
import cn.issboy.mengine.core.planner.Planner;
import cn.issboy.mengine.core.planner.plan.PlanNode;
import cn.issboy.mengine.core.util.MonitorMetadata;
import cn.issboy.mengine.core.util.StringUtil;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;

/**
 * created by just on 18-1-5
 */

public class MEngine {
    Logger logger = LoggerFactory.getLogger(this.getClass());

    private static final String JAR_FILE_NAME = StringUtil.formatDir("/home/just/IdeaProjects/MEngine/kstream-app/target/kstream-app-template-1.0-SNAPSHOT-jar-with-dependencies.jar");
    private static final String FINAL_JAR_NAME = StringUtil.formatDir("monitor-kStream-application.jar");
    private static final String MAIN = StringUtil.formatDir("template/Main.vm");
    private static final String DOCKERFILE_TEMPLATE = StringUtil.formatDir("template/Dockerfile.vm");

    public static class MEngineHolder {
        private static final MEngine singleEngine = new MEngine();
    }

    public static MEngine getSingletonEngine() {
        return MEngineHolder.singleEngine;
    }

    private final MetaStore metaStore;

    private MEngine() {
        this.metaStore = new MetaStoreImpl();
    }

    public MetaStore getMetaStore() {
        return metaStore;
    }


    public String buildJar(Map<String, Object> props, BlockGroup monitorContext) throws Exception {
        // 拷贝一份,在analyze的时候会变更旧视图(加Filed)
        MetaStore tmpMetaStore = metaStore.clone();
        List<Analysis> analysisGroup = new MonitorAnalyzer(tmpMetaStore).analyze(monitorContext);

        MonitorMetadata metadata = new MonitorMetadata();
        metadata.setBootstrapServers(props.get("bootstrapServers").toString());
        metadata.setSchemaRegistry(props.get("schemaRegistry").toString());
        String monitorGroupId = props.get("monitorGroupId").toString();

        for (Analysis analysis : analysisGroup) {
            PlanNode plan = new Planner(analysis).buildPlan();
            metadata.addMonitorId(monitorGroupId + analysis.getMonitorId().toString());

            MonitorKStreamBuilder streamBuilder = plan.buildDSL(new StringBuilder(), props);
            metadata.addDSLCode(streamBuilder.getDslBuilder().toString());
        }

        StringBuilder path = new StringBuilder();
        path.append(props.get("userId").toString())
                .append("/")
                .append(monitorGroupId)
                .append(DateTimeFormatter.ofPattern("-yyyyMMdd/").withZone(ZoneId.systemDefault()).format(Instant.now()));

        TemplateResolver resolver = new TemplateResolver();
        String javaCode = resolver.resolveMetadata(metadata, "MonitorMetadata", MAIN);
        logger.info(javaCode);
        long tmp = Instant.now().toEpochMilli();
        Map<String, byte[]> classMap = StringCompiler.newInstance().compile("Main.java", javaCode);
        logger.info("compile Time : " + (Instant.now().toEpochMilli() - tmp));
        String nfsPath = props.get("nfsPath").toString() + path.toString();
        tmp = Instant.now().toEpochMilli();
        createJar(nfsPath, classMap);
        logger.info("jar create Time : " + (Instant.now().toEpochMilli() - tmp));

        String dockerfile = resolver.resolveMetadata("80", "dockerPort", DOCKERFILE_TEMPLATE);
        logger.info(dockerfile);
        writeDocker(nfsPath + "Dockerfile",dockerfile);

        // user/MonitorId-time/
        return path.toString();
    }


    // TODO Performance optimizing.
    // cannot copy tmp to existed jar because {new JarOutputStream} will clear class files.
    private boolean createJar(String destPath, Map<String, byte[]> classMap) throws IOException {
        // Create file descriptors for the jar and a temp jar.

        File jarFile = new File(JAR_FILE_NAME);
        createFolder(destPath);
        File tmpJarFile = new File(destPath + "monitor-kStream-application.jar");

        // Open the jar file.

        JarFile jar = new JarFile(jarFile);
        logger.info(JAR_FILE_NAME + " opened.");

        // Initialize a flag that will indicate that the jar was updated.

        boolean jarUpdated = false;
        byte[] buffer = new byte[1024 * 4];
        int bytesRead;

        try {
            // Create a temp jar file with no manifest. (The manifest will
            // be copied when the entries are copied.)

            JarOutputStream tempJar =
                    new JarOutputStream(new FileOutputStream(tmpJarFile));
            try {
                // Create a jar entry and add it to the temp jar.
                for (Map.Entry<String, byte[]> clazz : classMap.entrySet()) {
                    String fileName = clazz.getKey().replace(".", "/") + ".class";
                    byte[] buf = clazz.getValue();

                    JarEntry entry = new JarEntry(fileName);
                    tempJar.putNextEntry(entry);
                    // Read the file and write it to the jar.

                    tempJar.write(buf);

                }

                // Loop through the jar entries and add them to the temp jar,
                // skipping those entries which are under cn.issboy.streamapp
                for (Enumeration entries = jar.entries(); entries.hasMoreElements(); ) {

                    JarEntry entry = (JarEntry) entries.nextElement();

                    if (!entry.getName().contains("cn/issboy/streamapp/Main")) {
                        // Get an input stream for the entry.

                        InputStream entryStream = jar.getInputStream(entry);

                        // Read the entry and write it to the temp jar.

                        tempJar.putNextEntry(entry);

                        while ((bytesRead = entryStream.read(buffer)) != -1) {
                            tempJar.write(buffer, 0, bytesRead);
                        }
                    }
                }

                jarUpdated = true;
            } catch (IOException e) {
                e.printStackTrace();

                // Add a stub entry here, so that the jar will close without an
                // exception.
                tempJar.putNextEntry(new JarEntry("stub"));
            } finally {
                tempJar.close();
            }
        } finally {
            jar.close();
            logger.info(JAR_FILE_NAME + " closed.");

            // If the jar was not updated, delete the temp jar file.

            if (!jarUpdated) {
                tmpJarFile.delete();
            }
        }

        return jarUpdated;
    }

    private void createFolder(String folderName){
        File folder = new File(folderName);
        if(!folder.exists()){
            while(!folder.mkdirs()){;}
        }

    }

    private void writeDocker(String dockerPath,String dockerfile){
        FileOutputStream fileOutputStream;
        try {
            fileOutputStream =  new FileOutputStream(dockerPath);
            fileOutputStream.write(dockerfile.getBytes());
            fileOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
