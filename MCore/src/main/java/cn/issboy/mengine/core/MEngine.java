package cn.issboy.mengine.core;

import avro.shaded.com.google.common.annotations.VisibleForTesting;
import cn.issboy.mengine.core.analyzer.Analysis;
import cn.issboy.mengine.core.analyzer.MonitorAnalyzer;
import cn.issboy.mengine.core.codegen.MonitorKStreamBuilder;
import cn.issboy.mengine.core.codegen.StringCompiler;
import cn.issboy.mengine.core.codegen.TemplateResolver;
import cn.issboy.mengine.core.exception.MException;
import cn.issboy.mengine.core.metastore.MetaStore;
import cn.issboy.mengine.core.metastore.MetaStoreImpl;
import cn.issboy.mengine.core.parser.BlockGroup;
import cn.issboy.mengine.core.planner.Planner;
import cn.issboy.mengine.core.planner.plan.PlanNode;
import cn.issboy.mengine.core.util.MonitorMetadata;
import cn.issboy.mengine.core.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.tools.jar.Main;

import java.io.*;
import java.nio.file.*;
import java.time.Instant;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;

/**
 * created by just on 18-1-5
 */

public class MEngine {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public static AtomicInteger monitorSeqNum = new AtomicInteger(0);
    private String jarFilePath; //= StringUtils.replaceSeparator("/home/just/IdeaProjects/MEngine/kstream-app/target/kstream-app-template-1.0-SNAPSHOT-jar-with-dependencies.jar");
    private static final String FINAL_JAR_NAME = StringUtils.replaceSeparator("monitor-kStream-application.jar");
    private static final String MAIN = StringUtils.replaceSeparator("template/Main.vm");
    private static final String DOCKERFILE_TEMPLATE = StringUtils.replaceSeparator("template/Dockerfile.vm");

    private static class MEngineHolder {
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


    public String buildJar(Map<String, Object> props, BlockGroup monitorContext) throws IOException,MException {
        monitorSeqNum.set(0);

        List<Analysis> analysisGroup = new MonitorAnalyzer(metaStore).analyze(monitorContext);

        MonitorMetadata metadata = new MonitorMetadata();
        metadata.setBootstrapServers(props.get("bootstrapServers").toString());
        metadata.setSchemaRegistry(props.get("schemaRegistry").toString());
        String monitorGroupId = props.get("monitorGroupId").toString();
        jarFilePath = StringUtils.replaceSeparator(props.get("jarPath").toString());

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
                .append("/");

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

        String dockerfile = resolver.resolveMetadata("82", "dockerPort", DOCKERFILE_TEMPLATE);
        logger.info(dockerfile);
        while(!writeDocker(nfsPath + "Dockerfile", dockerfile));

        // user/MonitorId-time/
        return path.toString();
    }

    @Deprecated
    // using tool provided by sun in rt.jar, no need to call by reflect -_- zzz.
    // if you don't want to change the original jar file, u can modify the code in run() to add a path argument.
    private void updateAndCopyJar(String jarFilePath, String destPath, Map<String, byte[]> classMap) {
        Main main = new Main(System.out, System.err, "jar");
        String[] args = new String[classMap.size() * 3 + 2];
        args[0] = "uf";// update flag.
        args[1] = jarFilePath;
        int i = 2;


            // write class files to file system.
            long start = Instant.now().toEpochMilli();
            for (Map.Entry<String, byte[]> clazz : classMap.entrySet()) {
                args[i++] = "-C";
                args[i++] = Paths.get(jarFilePath).getParent().toString();
                String fileName = clazz.getKey().replace(".", "/") + ".class";
                args[i++] = fileName;
                createFolder(StringUtils.replaceSeparator(args[3] + "/" + Paths.get(fileName).getParent().toString()));
                byte[] bytecode = clazz.getValue();

                try(FileOutputStream fops = new FileOutputStream(StringUtils.replaceSeparator(args[3] + "/" + fileName))){
                    fops.write(bytecode);
                }catch (FileNotFoundException e){
                    logger.error("Failed to open file : " ,fileName);
                    throw new MException("Failed to open file :" + fileName);
                }catch (IOException e ){
                    logger.error("I/O operation failed on file : {}",fileName);
                    throw new MException("Failed to open file :" + fileName);
                }

            }
            // update jar file.
            main.run(args);
            Path path = Paths.get(jarFilePath);
            createFolder(destPath);
            try(FileOutputStream fileOutputStream = new FileOutputStream(destPath + FINAL_JAR_NAME)){
                Files.copy(path,fileOutputStream);
            } catch (IOException e) {
            logger.info(e.getMessage());
            throw new MException(String.format("Failed to copy file from %s to %s",jarFilePath,destPath));
        }


    }

    // TODO performance tuning
    // cannot copy tmp to existed jar because {new JarOutputStream} will clear class files.
    private boolean createJar(String destPath, Map<String, byte[]> classMap) throws IOException {
        // Create file descriptors for the jar and a temp jar.
        File jarFile = new File(jarFilePath);
        createFolder(destPath);
        File newJarFile = new File(destPath + FINAL_JAR_NAME);

        // Open the jar file.
        JarFile jar = new JarFile(jarFile);
        logger.info(jarFilePath + " opened.");

        // Initialize a flag that will indicate that the jar was updated.
        boolean jarUpdated = false;
        byte[] buffer = new byte[1024 * 8];
        int bytesRead;

        try {
            // Create a temp jar file with no manifest. (The manifest will
            // be copied when the entries are copied.)
            JarOutputStream newJar =
                    new JarOutputStream(new FileOutputStream(newJarFile));
            try {

                // Create a jar entry and add it to the temp jar.
                for (Map.Entry<String, byte[]> clazz : classMap.entrySet()) {
                    String fileName = clazz.getKey().replace(".", "/") + ".class";
                    byte[] buf = clazz.getValue();

                    JarEntry entry = new JarEntry(fileName);
                    newJar.putNextEntry(entry);
                    // Read the file and write it to the jar.
                    newJar.write(buf);

                }
                // Loop through the jar entries and add them to the temp jar,
                // skipping those entries which are under cn.issboy.streamapp
                for (Enumeration entries = jar.entries(); entries.hasMoreElements(); ) {
                    JarEntry entry = (JarEntry) entries.nextElement();

                    if (!entry.getName().contains("cn/issboy/streamapp/Main")) {
                        // Get an input stream for the entry.
                        InputStream entryStream = jar.getInputStream(entry);

                        // Read the entry and write it to the temp jar.
                        newJar.putNextEntry(entry);

                        while ((bytesRead = entryStream.read(buffer)) != -1) {
                            newJar.write(buffer, 0, bytesRead);
                        }
                    }
                }
                jarUpdated = true;
            } catch (IOException e) {
                logger.error("error while creating jar",e);
                // Add a stub entry here, so that the jar will close without an
                // exception.
                newJar.putNextEntry(new JarEntry("stub"));
                throw new MException("error while creating jar");
            } finally {
                newJar.close();
            }
        } finally {
            jar.close();
            logger.info(jarFilePath + " closed.");

            // If the jar was not updated, delete the temp jar file.
            if (!jarUpdated) {
                newJarFile.delete();
            }
        }
        return jarUpdated;
    }

    @VisibleForTesting
    void createFolder(String folderName) {
        File folder = new File(folderName);
        if (!folder.exists()) {
            while (!folder.mkdirs()) ;
        }

    }

    private boolean writeDocker(String dockerPath, String dockerfileContent) {
        FileOutputStream fops  = null;
        try  {
            fops = new FileOutputStream(dockerPath);
            fops.write(dockerfileContent.getBytes());
            return true;
        } catch (IOException e) {
            logger.info(e.getMessage());
            throw new MException(e.getMessage());
        }finally {
            if(fops !=null){
                try {
                    fops.close();
                } catch (IOException e) {
                    logger.error("failed to close {}",dockerPath);
                }
            }
        }
    }
}
