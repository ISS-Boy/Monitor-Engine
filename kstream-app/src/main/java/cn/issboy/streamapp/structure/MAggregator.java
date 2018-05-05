package cn.issboy.streamapp.structure;

import cn.issboy.streamapp.structure.GenericRow;

import cn.issboy.streamapp.structure.functions.AggFunction;
import cn.issboy.streamapp.structure.functions.FunctionRegistry;
import org.apache.kafka.streams.kstream.Aggregator;
import org.codehaus.commons.compiler.CompilerFactoryFactory;
import org.codehaus.commons.compiler.IExpressionEvaluator;

import java.util.ArrayList;

/**
 * created by just on 18-4-23
 */
public class MAggregator implements Aggregator<String, GenericRow, GenericRow> {

    private ArrayList<String> alias;
    private ArrayList<AggFunction> functions;
    private ArrayList<String> measures;
    private ArrayList<String> predicates;

    // TODO: 18-4-24 提供视图支持,支持多种数据类型和函数返回值,当前所有值都为Float
    // ArrayList<String> schemas;

    /**
     * Aggregator
     *
     * @param functionRegistry functionRegistry
     * @param aggContext       {alias0,function0,measure0,predicate0,alias1,function1,measure1,predicate1...}
     */
    public MAggregator(FunctionRegistry functionRegistry, String... aggContext) {
        alias = new ArrayList<>();
        functions = new ArrayList<>();
        measures = new ArrayList<>();
        predicates = new ArrayList<>();

        for (int i = 0; i < aggContext.length; i += 4) {
            alias.add(aggContext[i]);
            if (functionRegistry.getFunctionByName(aggContext[i + 1]) == null) {
                throw new IllegalArgumentException(String.format("function %s does not support yet", aggContext[i+1]));
            }
            functions.add(functionRegistry.getFunctionByName(aggContext[i + 1]).getInstance());
            measures.add(aggContext[i + 2]);
            predicates.add(aggContext[i + 3]);
        }

    }

    @Override
    public GenericRow apply(String key, GenericRow value, GenericRow aggregate) {
        aggregate.getValues().putAll(value.getValues());
        for (int i = 0; i < alias.size(); i++) {
            Object measureVal = value.getValues().get(measures.get(i));
            if (getPredicate(predicates.get(i), measureVal, measures.get(i))) {

                aggregate.getValues().put(alias.get(i), functions.get(i)
                        .apply(measureVal, aggregate.getValues().get(alias.get(i))));
            }

        }
        return aggregate;
    }

    private boolean getPredicate(String predicate, Object val, String name) {
        try {
            // 没有条件,返回true
            if (predicate.equals("")) {
                return true;
            }
            String[] values = {name};
            Class[] types = {Float.class};
            Object[] params = {val};


            IExpressionEvaluator ee = CompilerFactoryFactory.getDefaultCompilerFactory().newExpressionEvaluator();
            // 参数名和参数类型
            ee.setParameters(values, types);
            // 表达式返回值
            ee.setExpressionType(boolean.class);
            // 表达式(字符串)
            ee.cook(predicate);
            // 返回表达式求值结果
            return (boolean) ee.evaluate(params);
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate value for " + predicate);
        }
    }

}
