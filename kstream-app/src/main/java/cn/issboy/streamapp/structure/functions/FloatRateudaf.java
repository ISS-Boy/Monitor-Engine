package cn.issboy.streamapp.structure.functions;

/**
 * 可在{@link FunctionRegistry}加一个determiner来支持除Float之外的数据类型
 * created by just on 18-4-25
 */
public class FloatRateudaf extends AggFunction<Float,Float>{
    private Float pre;

    FloatRateudaf() {
        this.functionName = "rate";
        this.pre = 0f;
    }

    @Override
    public Float apply(Float val, Float agg) {

        if(agg == null){
            // 第一次的增幅/增比为0
            pre = val;
            return val - pre;
        }else{
            Float res = 100 * (val - pre)/pre;
            pre = val;
            return res;
        }
    }

    @Override
    public AggFunction<Float, Float> getInstance() {
        return new FloatRateudaf();
    }
}
