import org.junit.Test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;
import java.util.zip.ZipEntry;

/**
 * created by just on 18-1-9
 */
public class JarOutputTest {


    @Test
    public void jaroptstreamTest() throws IOException {

        JarOutputStream target = new JarOutputStream(new FileOutputStream("/home/just/IdeaProjects/MEngine/kstream-app/target/kstream-app-template-1.0-SNAPSHOT-jar-with-dependencies.jar"));
        ZipEntry entry = new ZipEntry("cn/issboy/streamapp/Main.class");
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
