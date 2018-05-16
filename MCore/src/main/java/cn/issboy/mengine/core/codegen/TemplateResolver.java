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

    private final VelocityEngine ve;
    private final VelocityContext ctx;

    public TemplateResolver() {
        ve = new VelocityEngine();
        ve.setProperty(RuntimeConstants.RESOURCE_LOADER,"classpath");
        ve.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
        ve.init();
        ctx = new VelocityContext();
    }

    public String resolveTemplate(final Object Metadata,String keyName,String templateName){
        Template template = ve.getTemplate(templateName);
        StringWriter sw = new StringWriter();

        ctx.put(keyName,Metadata);

        template.merge(ctx,sw);
        return sw.toString();
    }

}
