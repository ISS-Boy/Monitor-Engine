package cn.issboy.mengine.core;

import cn.issboy.mengine.core.codegen.StringCompiler;
import cn.issboy.mengine.core.codegen.TemplateResolver;
import cn.issboy.mengine.core.exception.BuildImgException;
import cn.issboy.mengine.core.structure.MonitorKStream;
import cn.issboy.mengine.core.util.MonitorMetadata;
import cn.issboy.mengine.parser.MParser;
import cn.issboy.mengine.parser.pojo.Monitor;
import com.spotify.docker.client.DockerClient;
import jdk.internal.dynalink.MonomorphicCallSite;
import jdk.internal.util.xml.impl.Input;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;
import java.util.Enumeration;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;

/**
 * created by just on 18-1-5
 */
public class MEngine {

    private static final String JAR_FILE_NAME = "../kstream-app/target/kstream-app-template-1.0-SNAPSHOT-jar-with-dependencies.jar";
    private static final String DOCKER_DIRECTORY = "../kstream-app";

    DockerClient dockerClient;

    public MEngine(final DockerClient dockerClient){
        this.dockerClient = dockerClient;
    }

    public void registerMonitor() {
    try{
        AtomicReference<String> imgIdFromMsg = new AtomicReference<>();
        final String retImgId = dockerClient.build(Paths.get(DOCKER_DIRECTORY),"test",(message)->{
            final String imgId = message.buildImageId();
            if(imgId!= null){
                imgIdFromMsg.set(imgId);
            }
        });
        dockerClient.push(retImgId);
        dockerClient.tag(retImgId,"test");





    }catch (Exception e){
        throw new BuildImgException("build image failed",e);
    }





    }

    public void startMonitor(){




    }

    public void closeMonitor(){





    }



    public void buildJar(InputStream xmlStream) throws IOException{
        Monitor monitor = getXmlBinder(xmlStream);
        String dslCode = new MonitorKStream(new StringBuilder()).format2DSL(monitor);
        MonitorMetadata metadata = new MonitorMetadata(dslCode,
                monitor.getDatasource().getKafka().getConnection());
        String javaCode = new TemplateResolver().resolve(metadata);
        Map<String,byte[]> classMap = StringCompiler.newInstance().compile("Main.java",javaCode);
        updateJar(JAR_FILE_NAME,classMap);
    }

    // TODO Performance optimizing.
    // cannot copy tmp to existed jar because {new JarOutputStream} will clear class files.
    public void updateJar(String jarName, Map<String, byte[]> classMap) throws IOException {
        // Create file descriptors for the jar and a temp jar.
        File jarFile = new File(jarName);
        File tempJarFile = new File(jarName + ".tmp");

        // Open the jar file.

        JarFile jar = new JarFile(jarFile);
        System.out.println(jarName + " opened.");

        // Initialize a flag that will indicate that the jar was updated.

        boolean jarUpdated = false;
        byte[] buffer = new byte[1024];
        int bytesRead;

        try {
            // Create a temp jar file with no manifest. (The manifest will
            // be copied when the entries are copied.)

            JarOutputStream tempJar =
                    new JarOutputStream(new FileOutputStream(tempJarFile));
            try {
                // Create a jar entry and add it to the temp jar.
                for (Map.Entry<String, byte[]> clazz : classMap.entrySet()) {
                    String fileName = clazz.getKey().replace(".","/")+".class";
                    byte[] buf = clazz.getValue();

                    JarEntry entry = new JarEntry(fileName);
                    tempJar.putNextEntry(entry);

                    // Read the file and write it to the jar.

                    tempJar.write(buf);

                }

                // Loop through the jar entries and add them to the temp jar,
                // skipping the entries that was under cn.issboy.streamapp
                for (Enumeration entries = jar.entries(); entries.hasMoreElements(); ) {

                    JarEntry entry = (JarEntry) entries.nextElement();

                    if (!entry.getName().contains("cn/issboy/streamapp")) {
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
            System.out.println(jarName + " closed.");

            // If the jar was not updated, delete the temp jar file.

            if (!jarUpdated) {
                tempJarFile.delete();
            }
        }

        // If the jar was updated, delete the original jar file and rename the
        // temp jar file to the original name.
        if (jarUpdated) {
            jarFile.delete();
            tempJarFile.renameTo(jarFile);
            System.out.println(jarName + " updated.");
        }
    }


    public Monitor getXmlBinder(InputStream xmlstream) {
        return new MParser().parserXml(xmlstream);
    }

}
