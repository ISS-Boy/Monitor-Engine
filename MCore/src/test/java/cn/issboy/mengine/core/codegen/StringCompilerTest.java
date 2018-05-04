package cn.issboy.mengine.core.codegen;

import cn.issboy.mengine.core.MEngine;
import cn.issboy.mengine.core.codegen.StringCompiler;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import javax.tools.JavaCompiler;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * created by just on 18-1-8
 */
public class StringCompilerTest {
    StringCompiler compiler;

    @Before
    public void initial() {
        compiler = StringCompiler.newInstance();
    }

    static final String JAVA_BEAN = "// ----- Test bean code------  \n"
            + "package cn.issboy.streamapp;                         \n"
            + "public class CountBean {                             \n"
            + "    String sex;                                      \n"
            + "    Long count;                                      \n"
            + "    public CountBean(String sex, Long count) {       \n"
            + "        this.sex = sex;                              \n"
            + "        this.count = count;                          \n"
            + "    }                                                \n"
            + "    public String getSex() {                         \n"
            + "        return sex;                                  \n"
            + "    }                                                \n"
            + "    public void setSex(String sex) {                 \n"
            + "        this.sex = sex;                              \n"
            + "    }                                                \n"
            + "    public Long getCount() {                         \n"
            + "        return count;                                \n"
            + "    }                                                \n"
            + "}                                                      ";


    @Test
    public void singleTest() throws IOException {
        Map<String ,byte[]> result = new HashMap<>();
        result =  compiler.compile("CountBean.java",JAVA_BEAN);
        Assert.assertEquals( 1,result.size());
        result.forEach((key,value)-> System.out.println(key+"\n"+value));
    }

    static final String MULTIPLE_JAVA = "/* a single class to many files */   \n"
            + "package cn.issboy.streamapp;                                   \n"
            + "import java.util.*;                                            \n"
            + "public class Multiple {                                        \n"
            + "    List<Bird> list = new ArrayList<Bird>();                   \n"
            + "    public void add(String name) {                             \n"
            + "        Bird bird = new Bird();                                \n"
            + "        bird.name = name;                                      \n"
            + "        this.list.add(bird);                                   \n"
            + "    }                                                          \n"
            + "    public Bird getFirstBird() {                               \n"
            + "        return this.list.get(0);                               \n"
            + "    }                                                          \n"
            + "    public static class StaticBird {                           \n"
            + "        public int weight = 100;                               \n"
            + "    }                                                          \n"
            + "    class NestedBird {                                         \n"
            + "        NestedBird() {                                         \n"
            + "            System.out.println(list.size() + \" birds...\");   \n"
            + "        }                                                      \n"
            + "    }                                                          \n"
            + "}                                                              \n"
            + "/* package level */                                            \n"
            + "class Bird {                                                   \n"
            + "    String name = null;                                        \n"
            + "}                                                              \n";


    @Test
    public void mutipleTest() throws IOException{
        Map<String ,byte[]> result = new HashMap<>();
        result =  compiler.compile("Multiple.java",MULTIPLE_JAVA);
        Assert.assertEquals( 4,result.size());
        result.forEach((key,value)-> System.out.println(key+"\n"+value));
    }

    static final String DEPENDENCY_BEAN = "// --- Test bean code -- \n"
            + "package cn.issboy.streamapp;                         \n"
            + "import org.apache.kafka.streams.KafkaStreams;        \n"
            + "import org.apache.kafka.clients.consumer.*;          \n"
            + "public class CountBean {                             \n"
            + "    String sex;                                      \n"
            + "    Long count;                                      \n"
            + "    public CountBean(String sex, Long count) {       \n"
            + "        this.sex = sex;                              \n"
            + "        this.count = count;                          \n"
            + "    }                                                \n"
            + "    public String getSex() {                         \n"
            + "        return sex;                                  \n"
            + "    }                                                \n"
            + "    public void setSex(String sex) {                 \n"
            + "        this.sex = sex;                              \n"
            + "    }                                                \n"
            + "    public Long getCount() {                         \n"
            + "        return count;                                \n"
            + "    }                                                \n"
            + "}                                                      ";


    @Test
    public void denpendecyTest() throws IOException{
        Map<String ,byte[]> result = new HashMap<>();
        result =  compiler.compile("CountBean.java",DEPENDENCY_BEAN);
//        Assert.assertEquals( 4,result.size());
        result.forEach((key,value)-> System.out.println(key+"\n"+value));
    }


}
