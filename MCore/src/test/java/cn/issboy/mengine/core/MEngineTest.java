package cn.issboy.mengine.core;

import com.sun.deploy.util.ReflectionUtil;
import org.junit.Test;
import sun.tools.jar.Main;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
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

        mEngine.createFolder("/home/just/test");
        mEngine.createFolder("/home/just/test/test");
        File folder = new File("/home/just/test");
        File subFolder = new File("/home/just/test/test");
        File file = new File("/home/just/test/test/test");
        FileOutputStream writer = new FileOutputStream(file);
        writer.write("test".getBytes());
        folder.deleteOnExit();
        subFolder.deleteOnExit();
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
    public void updateJarReflectTest() throws Exception {
        File expandFile = new File("/home/just/IdeaProjects/MEngine/kstream-app/target/cn/issboy/streamapp/aa.class");
        mEngine.createFolder("/home/just/IdeaProjects/MEngine/kstream-app/target/cn/issboy/streamapp");
        FileOutputStream fops = new FileOutputStream(expandFile);
        fops.write("test".getBytes());

        Object[] objects = {System.out,System.err,"jar"};
        Class[] clazz = {PrintStream.class,PrintStream.class,String.class};
        Object main1 = ReflectionUtil.createInstance("sun.tools.jar.Main",clazz,objects,this.getClass().getClassLoader());

        ReflectionUtil.invoke(main1,"run",new Class[]{String[].class},
                new Object[]{new String[]{"uf","/home/just/IdeaProjects/MEngine/kstream-app/target/kstream-app-template-1.0-SNAPSHOT-jar-with-dependencies.jar",
                        "-C","/home/just/IdeaProjects/MEngine/kstream-app/target","cn/issboy/streamapp/aa.class"}});
        fops.close();
        expandFile.deleteOnExit();
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


}
