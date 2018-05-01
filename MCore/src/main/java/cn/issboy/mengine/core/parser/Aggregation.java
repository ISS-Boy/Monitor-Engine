package cn.issboy.mengine.core.parser;

import java.util.List;

/**
 * Auto-generated: 2018-04-24 20:24:26
 *
 * @author bejson.com (i@bejson.com)
 * @website http://www.bejson.com/java2pojo/
 */
public class Aggregation extends Node {

    private Window window;
    private List<AggregationValues> aggregationValues;

    public void setWindow(Window window) {
        this.window = window;
    }

    public Window getWindow() {
        return window;
    }

    public void setAggregationValues(List<AggregationValues> aggregationValues) {
        this.aggregationValues = aggregationValues;
    }

    public List<AggregationValues> getAggregationValues() {
        return aggregationValues;
    }

    @Override
    protected void accept(MonitorVisitor visitor) {
        visitor.visitAggregation(this);
    }

    @Override
    public String toString() {
        return "Aggregation{" +
                "window=" + window +
                ", aggregationValues=" + aggregationValues +
                '}';
    }

}
