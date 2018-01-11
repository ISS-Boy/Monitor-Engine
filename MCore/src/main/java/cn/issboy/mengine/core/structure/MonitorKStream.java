package cn.issboy.mengine.core.structure;

import cn.issboy.mengine.parser.pojo.FilterType;
import cn.issboy.mengine.parser.pojo.JoinType;
import cn.issboy.mengine.parser.pojo.Monitor;
import com.sun.istack.internal.NotNull;
import cn.issboy.mengine.core.util.StringUtil;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * TODO build Query Plan
 * created by just on 18-1-4
 */
public final class MonitorKStream {
    StringBuilder dslBuilder;

    public MonitorKStream(StringBuilder dslBuilder) {
        this.dslBuilder = dslBuilder;
    }

    /**
     * operation sequence:
     * Join/source --> filter --> aggregate --> output
     *
     * @param monitor
     * @return
     */
    public String format2DSL(Monitor monitor) {
        // TODO UUId needed.
        String outputTopic = "monitorId"+ LocalDate.now();
        for (Monitor.MonitorQueries.Query query : monitor.getMonitorQueries().getQuery()) {
            JoinType joinInfo;

            if ((joinInfo = query.getOperation().getJoin()) != null) {
                for(JoinType.JoinTargets.Target target : joinInfo.getJoinTargets().getTarget()){
                    String leftTopic = target.getLeft();
                    String rightTopic = target.getRight();
                    initSerde(leftTopic);
                    initSerde(rightTopic);
                    FilterType.Filters.Filter leftFilterInfo = null;
                    FilterType.Filters.Filter rightFilterInfo = null;
                    if (query.getOperation().getFilter() != null) {

                        for (FilterType.Filters.Filter filterInfo : query.getOperation().getFilter().getFilters().getFilter()) {

                            String measure = filterInfo.getValue();
                            // TODO deal with "topic" and "measure", same with join
                            if (StringUtil.compare(leftTopic,measure)) {
                                leftFilterInfo = filterInfo;
                            } else if (StringUtil.compare(leftTopic,measure)) {
                                rightFilterInfo = filterInfo;
                            }
                        }

                    }
                    switch (joinInfo.getJoinType()) {

                        case "Dynamic-Join-Dynamic":

                            if(leftFilterInfo == null){
                                stream(leftTopic, ";\n");
                            }else{
                                stream(leftTopic,"");
                                filter(leftFilterInfo);
                            }
                            if(rightFilterInfo == null){
                                stream(rightTopic, ";\n");
                            }else{
                                stream(rightTopic,"");
                                filter(rightFilterInfo);
                            }
                            ssJoin(target);
                    }

                }

            }

            into(outputTopic);
        }

        return this.dslBuilder.toString();
}

    // TODO what if no other operations -> whether ";\n" needed.

    public void stream(String topic, String endOfLine) {
        dslBuilder.append("KStream<String,MEvent> ")
                .append(topic.replace("-","")).append("Stream")
                .append("= builder.stream(Serdes.String(),")
                .append(topic.replace("-","")).append("Serde,")
                .append("\"").append(topic).append("\"")
                .append(")").append(endOfLine);

    }

    public void table(String topic) {


    }

    // TODO what if keySerde involved -> whether init here?
    // TODO Mhealth or Synthea -> build schema abstraction
    public void initSerde(String topic) {
        dslBuilder.append("final SpecificAvroSerde<MEvent> ")
                .append(topic.replace("-","")).append("Serde")
                .append(" = new SpecificAvroSerde<>();\n")
                .append(topic.replace("-","")).append("Serde")
                .append(".configure(serdeConfig,false);\n");
    }

    /**
     * filter source code example:
     * KStream<String, MEvent> heartStream = builder.stream(Serdes.String(), mEventSerde, HEART_RATE)
     * .filter((key, value) -> value.getMeasures().get("heart-rate").getValue() >= 120);
     */
    @NotNull
    public void filter(final FilterType.Filters.Filter filterInfo) {
        String measure = filterInfo.getValue();
        String threshold = filterInfo.getThrehold();
        String predicate = "(key,value)-> true";
        switch (filterInfo.getOp()) {
            case "LQ":
                predicate = "(key,value) -> value.getMeasures().get(\"" + measure + "\").getValue() >= " + threshold;
                break;
            case "EQ":
                predicate = "";
                break;
            case "SQ":
                break;
        }
        dslBuilder.append(".filter(")
                .append(predicate)
                .append(");\n");
    }

    /**
     * KStream<String, String> ssJoin = bldStream.ssJoin(heartStream, (left, right) -> "heart-rate= " + left.getMeasures().get("heart-rate").getValue() + "blood-pressure = " + right.getMeasures().get("blood-pressure-systolic").getValue(),
     * JoinWindows.of(TimeUnit.MINUTES.toMillis(5)),
     * Serdes.String(),
     * mEventSerde,
     * mEventSerde
     * );
     * TODO stream or table Join
     * @return
     */
    public void ssJoin(JoinType.JoinTargets.Target joinInfo) {
        String leftTopic  = joinInfo.getLeft();
        String rightTopic = joinInfo.getRight();

        String joiner = String.format("(left,right)-> {return \"%s = \" + left.getMeasures().get(\"heart_rate\").getValue() + right.getMeasures().get(\"systolic_blood_pressure\").getValue();}",leftTopic);

        switch (joinInfo.getJoinType()){
            case "inner":
                dslBuilder.append("KStream<String,String> joined = ")
                .append(leftTopic.replace("-","")).append("Stream.join(")
                .append(rightTopic.replace("-","")).append("Stream,")
                .append(joiner).append(",")
                .append("JoinWindows.of(TimeUnit.MINUTES.toMillis(5)),")
                .append("Serdes.String(),")
                .append(leftTopic.replace("-","")).append("Serde,")
                .append(rightTopic.replace("-","")).append("Serde);\n");

        }

    }

    public void into(String topic) {

        dslBuilder.append("joined.to(Serdes.String(),Serdes.String(),\"")
        .append(topic).append("\");\n");
    }


}
