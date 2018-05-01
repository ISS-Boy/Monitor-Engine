package cn.issboy.streamapp.structure.functions;

/**
 * created by just on 18-4-25
 */
public class FloatMinudaf extends AggFunction<Float,Float>{


    public FloatMinudaf() {
        this.functionName = "min";
    }
    @Override
    public Float apply(Float val, Float agg) {
        return val < agg ? val : agg;
    }

    @Override
    public AggFunction<Float, Float> getInstance() {
        return new FloatMinudaf();
    }
}

