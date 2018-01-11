import cn.issboy.mengine.core.MEngine;
import cn.issboy.mengine.core.structure.MonitorKStream;
import cn.issboy.mengine.parser.MParser;
import cn.issboy.mengine.parser.pojo.Monitor;
import org.junit.Test;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

/**
 * created by just on 18-1-8
 */
public class DSLBuilderTest {


    @Test
    public void joinerTest(){
        String leftTopic = "heart-rate";
        String rightTopic = "blood-pressure";

        String joiner = String.format("(left,right)-> {\"%s = \" + left.getMeasures().get(\"%s\").getValue() + right.getMeasures().get(\"%s\").getValue()}",leftTopic,leftTopic,rightTopic);
        System.out.println(joiner);
    }

    @Test
    public void builderTest() throws Exception{

        Monitor monitor = new MParser().parserXml(this.getClass().getClassLoader().getResourceAsStream("monitor/MonitorExample.xml"));
        String dslCode =  new MonitorKStream(new StringBuilder()).format2DSL(monitor);
        System.out.println(dslCode);
    }

}
