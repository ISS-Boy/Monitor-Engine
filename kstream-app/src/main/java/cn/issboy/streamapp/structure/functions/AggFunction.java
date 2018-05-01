package cn.issboy.streamapp.structure.functions;

/**
 * created by just on 18-4-24
 */
public abstract class AggFunction<V,A> {

    String functionName;

    public String getFunctionName() {
        return functionName;
    }

    public abstract A apply(V val,A agg);

    // rate,average函数带状态,需要每次初始化一个新实例.
    // TODO: 18-4-25 设计一个Stateless的函数表示方式,并能符合对不同种函数的抽象
    public abstract AggFunction<V,A> getInstance();



}
