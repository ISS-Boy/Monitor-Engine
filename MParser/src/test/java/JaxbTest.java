import cn.issboy.mengine.parser.pojo.FilterType;
import cn.issboy.mengine.parser.pojo.Monitor;
import org.junit.Test;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

/**
 * created by just on 18-1-3
 */
public class JaxbTest {


    @Test
    public void xmlParserTest() throws Exception{
        JAXBContext cxt = JAXBContext.newInstance(Monitor.class);
        Unmarshaller unmarshaller = cxt.createUnmarshaller();
        Monitor monitor = (Monitor) unmarshaller.unmarshal(this.getClass().getClassLoader().getResourceAsStream("monitor/MonitorExample.xml"));
        if(monitor!=null){
            for(Monitor.MonitorQueries.Query q : monitor.getMonitorQueries().getQuery()){

                for(FilterType.Filters.Filter filter : q.getOperation().getFilter().getFilters().getFilter()){
                    System.out.println("filter:"+filter.getOp()+" "+filter.getThrehold()+" "+filter.getValue());
                }

                System.out.println("id : "+ q.getId() );
                System.out.println("op : "+ q.getOperation().getJoin().getJoinType() );
                System.out.println("target : "+ q.getTarget().getDatatype() );
            }
            System.out.println( monitor.getDatasource().getKafka().getConnection().getValueSerializer());
            System.out.println( monitor.getDatasource().getKafka().getConnection().getValueDeserializer());
        }



    }
}
