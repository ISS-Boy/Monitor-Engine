import cn.issboy.mengine.core.MEngine;
import cn.issboy.mengine.core.codegen.TemplateResolver;
import cn.issboy.mengine.core.structure.MonitorKStream;
import cn.issboy.mengine.core.util.MonitorMetadata;
import cn.issboy.mengine.parser.MParser;
import cn.issboy.mengine.parser.pojo.Monitor;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.StringWriter;

/**
 * created by just on 18-1-3
 */
public class VelocityTest {

    private VelocityEngine ve;
    private VelocityContext ctx;
    @Before public void initial(){
        ve = new VelocityEngine();
        ve.setProperty(RuntimeConstants.RESOURCE_LOADER,"classpath");
        ve.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
        ve.init();
        ctx = new VelocityContext();
    }


    @Test
    public void velocityTest() throws IOException {

        Template test = ve.getTemplate("template/Main.vm");
        StringWriter sw = new StringWriter();
        Monitor monitor= new MParser().parserXml(this.getClass().getClassLoader().getResourceAsStream("monitor/MonitorExample.xml"));
        String dslCode = new MonitorKStream(new StringBuilder()).format2DSL(monitor);
        MonitorMetadata metadata = new MonitorMetadata(dslCode,
                monitor.getDatasource().getKafka().getConnection());
        ctx.put("MonitorMetadata",metadata);

        test.merge(ctx,sw);

        System.out.println(sw.toString());



    }

    @Test
    public void ifElseTest(){
        Template test = ve.getTemplate("template/ifelse.vm");
        StringWriter sw = new StringWriter();

//        ctx.put("serializer","String");
        ctx.put("external","AVRO");

        test.merge(ctx,sw);

        System.out.println(sw.toString());

    }

}
