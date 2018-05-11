package cn.issboy.mengine.core.analyzer;

import cn.issboy.mengine.core.metastore.SchemadDataSource;
import cn.issboy.mengine.core.planner.plan.JoinNode;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * created by just on 18-2-26
 */
public class Analysis {


    private StringBuilder monitorId = new StringBuilder();

    private JoinNode join;

    // pair of source,alias
    private List<Pair<SchemadDataSource,String>> sources = new LinkedList<>();

    private StringBuilder initContext = new StringBuilder();

    private StringBuilder aggContext = new StringBuilder();

    private String window = null;

    private List<String> predicates = new ArrayList<>();

    // data duplicated, remove later.
    private List<String> measures = new ArrayList<>();

    private List<String> selectFileds = new ArrayList<>();

    public StringBuilder getMonitorId() {
        return monitorId;
    }

    public void setMonitorId(StringBuilder monitorId) {
        this.monitorId = monitorId;
    }

    public JoinNode getJoin() {
        return join;
    }

    public void setJoin(JoinNode join) {

        this.join = join;
    }

    public List<Pair<SchemadDataSource, String>> getSources() {
        return sources;
    }

    public void addSources(Pair<SchemadDataSource, String> source) {
        switch (source.getKey().getDataType()){
            case STREAM:
                sources.add(0,source);
                break;
            case TABLE:
                sources.add(sources.size(),source);
                break;
        }
    }

    public StringBuilder getInitContext() {
        return initContext;
    }

    public StringBuilder getAggContext() {
        return aggContext;
    }

    public String getWindow() {
        return window;
    }

    public void setWindow(String window) {
        this.window = window;
    }

    public List<String> getSelectFileds() {
        return selectFileds;
    }

    public void addField(String filed) {
        selectFileds.add(filed);
    }

    public List<String> getMeasures() {
        return measures;
    }

    public void addMeasure(String measure){
        measures.add(measure);
    }

    public List<String> getPredicates() {
        return predicates;
    }

    public void addPredicate(String predicate) {
        this.predicates.add(predicate);
    }
}
