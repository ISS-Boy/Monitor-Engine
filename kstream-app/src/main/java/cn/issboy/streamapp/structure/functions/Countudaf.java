package cn.issboy.streamapp.structure.functions;

/**
 * created by just on 18-4-25
 */
public class Countudaf extends AggFunction<Object,Float>{


    public Countudaf() {
        this.functionName = "count";
    }

    @Override
    public Float apply(Object val, Float agg) {
        return agg + 1f;
    }


    @Override
    public AggFunction<Object, Float> getInstance() {
        return new Countudaf();
    }
}
