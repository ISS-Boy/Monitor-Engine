package cn.issboy.mengine.parser;

import cn.issboy.mengine.parser.pojo.Monitor;
import cn.issboy.mengine.parser.exception.ParseFailedException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.InputStream;

/**
 * created by just on 18-1-3
 */
public class MParser {


    public Monitor parserXml(InputStream is) {

        try {
            JAXBContext cxt = JAXBContext.newInstance(Monitor.class);
            Unmarshaller unmarshaller = cxt.createUnmarshaller();
            return (Monitor) unmarshaller.unmarshal(is);
        } catch (JAXBException e) {
            throw new ParseFailedException(e.getMessage(),e);
        }


    }


}
