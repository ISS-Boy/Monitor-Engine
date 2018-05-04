package cn.issboy.mengine.core.codegen;

import java.io.*;
import java.net.URI;
import java.nio.CharBuffer;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;
import java.util.zip.ZipEntry;

import javax.tools.*;
import javax.tools.JavaFileObject.Kind;

/**
 * created by just on 18-1-8
 */

class MemoryJavaFileManager extends ForwardingJavaFileManager<JavaFileManager> {

    // compiled classes in bytes:
    final Map<String, byte[]> classBytes = new HashMap<String, byte[]>();

    MemoryJavaFileManager(JavaFileManager fileManager) {
        super(fileManager);
    }

    public Map<String, byte[]> getClassBytes() {
        return new HashMap<String, byte[]>(this.classBytes);
    }

    @Override
    public void flush() throws IOException {
    }

    @Override
    public void close() throws IOException {
        classBytes.clear();
    }


    /**
     *  called by Javac.jvm.ClassWriter.writeClass(ClassSymbol c)
     *  JavaFileObject outFile = fileManager.getJavaFileForOutput(args...);
     *  OutputStream out = outFile.openOutputStream();
     *  try{writeClassFile(out,c);
     *      out.close();
     *  }
     */
    @Override
    public JavaFileObject getJavaFileForOutput(JavaFileManager.Location location, String className, Kind kind,
                                               FileObject sibling) throws IOException {
        if (kind == Kind.CLASS) {
            return new MemoryOutputJavaFileObject(className);
        } else {
            return super.getJavaFileForOutput(location, className, kind, sibling);
        }
    }

    JavaFileObject makeStringSource(String name, String code) {
        return new MemoryInputJavaFileObject(name, code);
    }

    /**
     * A file object used to represent source code coming from a string.
     */
    static class MemoryInputJavaFileObject extends SimpleJavaFileObject {

        final String code;

        MemoryInputJavaFileObject(String name, String code) {
            super(URI.create("string:///" + name), Kind.SOURCE);
            this.code = code;
        }

        @Override
        public CharBuffer getCharContent(boolean ignoreEncodingErrors) {
            return CharBuffer.wrap(code);
        }
    }

    /**
     * generate class byteCode to memory via ByteArrayOutputStream.
     * Implementation note:
     * a consequence of this requirement (it must support concurrent access to different file objects created by this object.)
     * is that a trivial implementation of output to a JarOutputStream is not a sufficient implementation.
     * That is, rather than creating a JavaFileObject that returns the JarOutputStream directly,
     * the contents must be cached until closed and then written to the JarOutputStream.
     * 摘自javadoc
     */
    class MemoryOutputJavaFileObject extends SimpleJavaFileObject {
        final String name;

        MemoryOutputJavaFileObject(String name) {
            super(URI.create("string:///" + name), Kind.CLASS);
            this.name = name;
        }

        @Override
        public OutputStream openOutputStream() {
            return new FilterOutputStream(new ByteArrayOutputStream()) {
                @Override

                public void close() throws IOException {
                    out.close();
                    ByteArrayOutputStream bos = (ByteArrayOutputStream) out;
                    classBytes.put(name, bos.toByteArray());
                }
            };
        }

    }

}

