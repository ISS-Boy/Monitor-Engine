package cn.issboy.mengine.core.parser;
import java.util.List;

/**
 * Auto-generated: 2018-04-24 20:24:26
 *
 * @author bejson.com (i@bejson.com)
 * @website http://www.bejson.com/java2pojo/
 */
public class BlockValues extends Node{

    private String monitorName;
    private List<Source> source;
    private Aggregation aggregation;
    private List<Filters> filters;
    private List<Selects> selects;
    public void setMonitorName(String monitorName) {
         this.monitorName = monitorName;
     }
     public String getMonitorName() {
         return monitorName;
     }

    public void setSource(List<Source> source) {
         this.source = source;
     }
     public List<Source> getSource() {
         return source;
     }

    public void setAggregation(Aggregation aggregation) {
         this.aggregation = aggregation;
     }
     public Aggregation getAggregation() {
         return aggregation;
     }

    public void setFilters(List<Filters> filters) {
         this.filters = filters;
     }
     public List<Filters> getFilters() {
         return filters;
     }

    public void setSelects(List<Selects> selects) {
         this.selects = selects;
     }
     public List<Selects> getSelects() {
         return selects;
     }

    @Override
    protected void accept(MonitorVisitor visitor) {
        visitor.visitBlockValues(this);
    }

    @Override
    public String toString() {
        return "BlockValues{" +
                "monitorName='" + monitorName + '\'' +
                ", source=" + source +
                ", aggregation=" + aggregation +
                ", filters=" + filters +
                ", selects=" + selects +
                '}';
    }
}