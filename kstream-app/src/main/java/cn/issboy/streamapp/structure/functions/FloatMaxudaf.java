package cn.issboy.streamapp.structure.functions;

/**
 * created by just on 18-5-7
 */
public class FloatMaxudaf extends AggFunction<Float,Float> {

    public FloatMaxudaf() {
        this.functionName = "max";
    }

    @Override
    public Float apply(Float val, Float agg) {
        return val > agg ? val : agg;
    }

    @Override
    public AggFunction<Float, Float> getInstance() {
        return new FloatMaxudaf();
    }
}
