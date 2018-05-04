package cn.issboy.mengine.core.codegen;

import cn.issboy.mengine.core.exception.CompileException;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;
import javax.tools.JavaCompiler.CompilationTask;

/**
 * created by just on 18-1-4
 */

public class StringCompiler {

    JavaCompiler compiler;
    StandardJavaFileManager stdManager;

    public static StringCompiler newInstance() {
        return new StringCompiler();
    }


    private StringCompiler() {
        this.compiler = ToolProvider.getSystemJavaCompiler();
        this.stdManager = compiler.getStandardFileManager(null, null, null);
    }

    public Map<String,byte[]> compile(String fileName,String code) throws IOException{

        try (MemoryJavaFileManager manager = new MemoryJavaFileManager(stdManager)) {
            JavaFileObject javaFileObject = manager.makeStringSource(fileName, code);
            CompilationTask task = compiler.getTask(null, manager, null, null, null, Arrays.asList(javaFileObject));
            Boolean result = task.call();
            if (result == null || !result ) {
                throw new CompileException("Compilation failed.");
            }
            return manager.getClassBytes();
        }

    }


    public Map<String,byte[]> compile(String fileName,String code,Iterable<String> options) throws IOException{
        if(options==null){
            return compile(fileName,code);
        }else{
            try (MemoryJavaFileManager manager = new MemoryJavaFileManager(stdManager)) {
                JavaFileObject javaFileObject = manager.makeStringSource(fileName, code);
                CompilationTask task = compiler.getTask(null, manager, null, options, null, Arrays.asList(javaFileObject));
                Boolean result = task.call();
                if (result == null || !result.booleanValue()) {
                    throw new CompileException("Compilation failed.");
                }
                return manager.getClassBytes();
            }
        }


    }


}
