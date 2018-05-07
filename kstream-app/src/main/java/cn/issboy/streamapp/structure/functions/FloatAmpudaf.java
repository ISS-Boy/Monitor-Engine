package cn.issboy.streamapp.structure.functions;

/**
 * 增幅
 * created by just on 18-5-4
 */
public class FloatAmpudaf extends AggFunction<Float,Float> {

    private Float pre;

    public FloatAmpudaf() {
        this.functionName = "amplitude";
        this.pre = 0f;
    }

    @Override
    public Float apply(Float val, Float agg) {
        if(agg == null){
            // 第一次的增幅为0
            pre = val;
            return val - pre;
        }else{
            Float res = val - pre;
            pre = val;
            return res;
        }
    }

    @Override
    public AggFunction<Float, Float> getInstance() {
        return new FloatAmpudaf();
    }
}
