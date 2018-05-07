package cn.issboy.streamapp.structure;

import org.apache.kafka.streams.kstream.Initializer;

import java.util.HashMap;
import java.util.Map;

/**
 * created by just on 18-2-27
 */
public class MInitializer implements Initializer<GenericRow> {


    private final Map<String,Object> columns;

    static class Fraction {
        Float numer;
        Float denom;
        Float val;
        public Fraction(Float numer, Float denom) {
            this.numer = numer;
            this.denom = denom;
            this.val = numer/denom;
        }

        public Float getNumer() {
            return numer;
        }

        public Float getDenom() {
            return denom;
        }

        public Float getVal() {
            return val;
        }
    }

    /**
     *
     * @param names pattern:{alias0,func0,alias1,func1...}
     */
    public MInitializer(String... names) {


        this.columns = new HashMap<>();
        for (int i = 0; i < names.length; i+=2) {
            switch (names[i + 1]){
                case "rate":
                case "amplitude":
                    columns.put(names[i],null);
                case "average":
                    columns.put(names[i],null);
                    // helper列用来求rate,average
                    // columns.put("helper",null);
                    break;
                case "min":
                    columns.put(names[i],Float.MAX_VALUE);
                    break;
                default:
                    columns.put(names[i],0f);
            }
        }
    }

    @Override
    public GenericRow apply() {
        Map<String,Object> columns = new HashMap<>(this.columns);
        return new GenericRow(columns);
    }
}
