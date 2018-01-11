package cn.issboy.mengine.core.codegen;

import cn.issboy.mengine.core.util.MonitorMetadata;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;

import java.io.StringWriter;

/**
 * created by just on 18-1-8
 */
public class TemplateResolver {

    private static final String MAIN = "template/Main.vm";

    private VelocityEngine ve;
    private VelocityContext ctx;

    public TemplateResolver() {
        ve = new VelocityEngine();
        ve.setProperty(RuntimeConstants.RESOURCE_LOADER,"classpath");
        ve.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
        ve.init();
        ctx = new VelocityContext();
    }

    public String resolve(final MonitorMetadata monitorMetadata){
        Template test = ve.getTemplate(MAIN);
        StringWriter sw = new StringWriter();

        ctx.put("MonitorMetadata",monitorMetadata);

        test.merge(ctx,sw);
        return sw.toString();
    }
}
