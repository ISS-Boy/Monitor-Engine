package cn.issboy.mengine.core.codegen;

import cn.issboy.mengine.core.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * TODO build Query Plan
 * created by just on 18-1-4
 */
public final class MonitorKStreamBuilder {
    final static Logger logger = LoggerFactory.getLogger(MonitorKStreamBuilder.class);

    private final StringBuilder dslBuilder;

    public MonitorKStreamBuilder(StringBuilder dslBuilder) {
        this.dslBuilder = dslBuilder;
    }

    public StringBuilder getDslBuilder() {
        return dslBuilder;
    }


    /**
     * Stream-Stream join ,always window-based
     * .join(stream,(left,right)->{},JoinWindows,serdes)
     */
    public MonitorKStreamBuilder streamJoin(MonitorKStreamBuilder builder) {
        dslBuilder.append(".join(")
                .append(builder.getDslBuilder().toString())
                // ValueJoiner
                .append(",(left,right) -> {GenericRow res = new GenericRow(new HashMap<>(left.getValues()));\n")
                .append("res.getValues().putAll(right.getValues());\n")
                .append("return res;},\n")
                // JoinWindows 10min by default
                .append("JoinWindows.of(TimeUnit.MINUTES.toMillis(10)),")
                // keySerde,LValueSerde,RValueSerde
                .append("Serdes.String(),monitorRowSerde,monitorRowSerde)");
        logger.info(dslBuilder.toString());
        return new MonitorKStreamBuilder(dslBuilder);
    }

    // non-window based
    public MonitorKStreamBuilder tableJoin(MonitorKStreamBuilder builder) {
        dslBuilder.append(".join(")
                .append(builder.getDslBuilder().toString())
                // ValueJoiner
                .append(",(left,right) -> {GenericRow res = new GenericRow(new HashMap<>(left.getValues()));\n")
                .append("res.getValues().putAll(right.getValues());\n")
                .append("return res;},\n")
                // keySerde,LValueSerde,RValueSerde
                .append("Serdes.String(),monitorRowSerde,monitorRowSerde)");

        return new MonitorKStreamBuilder(dslBuilder);
    }

    // .leftJoin(builder,(left,right)->{GenericRow res = new GenericRow(new HashMap<>(left.getValues()));
    //                      if(right!=null){
    //                          res.getValues().putAll(right.getValues());
    //                      }else{
    //                          return res;
    //                      }
    //                   },serdes)
    public MonitorKStreamBuilder leftJoin(MonitorKStreamBuilder builder) {
        dslBuilder.append(".leftJoin(")
                .append(builder.getDslBuilder().toString())
                // ValueJoiner
                .append(",(left,right) -> {GenericRow res = new GenericRow(new HashMap<>(left.getValues()));\n")
                .append("if(right != null){\n")
                .append("res.getValues().putAll(right.getValues());}else{\n")
                .append("return res;}},")
                // keySerde,LValueSerde,RValueSerde
                .append("Serdes.String(),monitorRowSerde,monitorRowSerde)");

        return new MonitorKStreamBuilder(dslBuilder);
    }

    // .groupByKey()
    // .aggregate(new MInitializer(initializer),new MAggregator(aggregator),TimeWindows,monitorRowSerde)
    // .toStream()

    public MonitorKStreamBuilder aggregate(String initializer, String aggregator, String window) {

        dslBuilder.append(".groupByKey()\n")
                .append(".aggregate(")
                .append("new MInitializer(")
                .append(StringUtil.trimLastSymbol(initializer))
                .append("),new MAggregator(functionRegistry,")
                .append(StringUtil.trimLastSymbol(aggregator))
                .append("),");
        if (window != null) {
            dslBuilder.append(window)
                    .append(",");
        }
        dslBuilder.append("monitorRowSerde)\n")
                .append(".toStream()\n");


        return new MonitorKStreamBuilder(dslBuilder);
    }

    // .filter((k,v) -> predicates)
    public MonitorKStreamBuilder filter(List<String> predicates) {

        dslBuilder.append(".filter((k,v)->");
        for (int i = 0; i < predicates.size(); i++) {
            if (i < predicates.size() - 1) {
                dslBuilder.append(predicates.get(i));
            } else {
                dslBuilder.append(StringUtil.trimLastSymbol(predicates.get(i)));
                dslBuilder.append(")\n");
            }
        }

        return new MonitorKStreamBuilder(dslBuilder);
    }

    // .mapValues(new SelectValueMapper("growth_systolic_blood_pressure", "timestamp"))
    public MonitorKStreamBuilder select(List<String> fields) {
        dslBuilder.append(".mapValues(")
                .append("new SelectValueMapper(");
        for (int i = 0; i < fields.size(); i++) {
            String field = fields.get(i);
            dslBuilder.append(field);
            if (!(i == fields.size() - 1)) {
                dslBuilder.append(",");
            }
        }
        dslBuilder.append("))\n");
        return new MonitorKStreamBuilder(dslBuilder);

    }

    // .map((k,v)-> KeyValue.pair(monitorId,v))
    // .to(Serdes.String(),monitorRowSerde,topic);
    public MonitorKStreamBuilder into(String monitorId, String topic) {

        dslBuilder.append(".map((k,v) -> KeyValue.pair(")
                .append(StringUtil.wrapString(monitorId))
                .append(",v))\n")
                .append(".to(Serdes.String(),monitorRowSerde,")
                .append(StringUtil.wrapString(topic))
                .append(");");
        logger.info(dslBuilder.toString());
        return new MonitorKStreamBuilder(dslBuilder);
    }


}
