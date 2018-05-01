package cn.issboy.streamapp.structure.functions;

/**
 * TODO 所有函数应该为stateless的,当前可能存在正确性问题,这个函数可能一个StreamTask(对应一个partition)只有一个实例,否则：
 * 一个partition内的数据有序,多个partition消费无序,无序的数据会导致错误,例子：
 * 旧窗口处理未结束,count状态应该为旧窗口的状态,此时新窗口的数据进入,调用initializer & aggregator,导致count被置0.
 * created by just on 18-4-25
 */
public class FloatAverageudaf extends AggFunction<Float,Float> {
    Long count;

    public FloatAverageudaf() {
        this.functionName = "average";
        this.count = 0l;
    }

    @Override
    public Float apply(Float val, Float agg) {
        if(agg == null){
            count = 0l;
            agg = 0f;
        }
        return (agg * count + val)/++count;
    }

    @Override
    public AggFunction<Float, Float> getInstance() {
        return new FloatAverageudaf();
    }

}
