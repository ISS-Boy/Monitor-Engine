package cn.issboy.streamapp.structure.functions;

import java.util.HashMap;
import java.util.Map;

/**
 * init的时候{@link FunctionRegistry#addFunction(AggFunction)}加determiner而不是func
 * getFunc的时候由determiner根据schema决定使用哪种类型(Float,Double……)的聚合函数
 * created by just on 18-4-23
 */
public class FunctionRegistry {

    public static class RegitsryHolder{
       private static final FunctionRegistry functionRegistry = new FunctionRegistry();
    }

    public static FunctionRegistry getFunctionRegistry(){
        return RegitsryHolder.functionRegistry;
    }
    private Map<String,AggFunction> functionMap = new HashMap<>();

    private FunctionRegistry() {
        init();
    }

    private void init() {
        // 把函数信息加入registry,调用时用getInstance获得一个新的函数实例
        addFunction(new FloatRateudaf());

        addFunction(new FloatMinudaf());

        addFunction(new FloatAverageudaf());

        addFunction(new Countudaf());


    }

    private void addFunction(AggFunction function){
        functionMap.put(function.getFunctionName(),function);
    }


    public AggFunction getFunctionByName(String funcName) {

        return functionMap.get(funcName);
    }

}
