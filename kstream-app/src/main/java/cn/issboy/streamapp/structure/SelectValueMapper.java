package cn.issboy.streamapp.structure;

import net.sourceforge.pinyin4j.PinyinHelper;
import org.apache.kafka.streams.kstream.ValueMapper;

import java.util.HashMap;
import java.util.Map;

/**
 * created by just on 18-1-29
 */
public class SelectValueMapper implements ValueMapper<GenericRow, GenericRow> {

    private String[] selectedValues;


    public SelectValueMapper(String... selectedValues) {
        this.selectedValues = selectedValues;

    }

    @Override
    public GenericRow apply(GenericRow genericRow) {

        printHelper("before", "select", genericRow, null);
        Map<String, Object> values = new HashMap<>();
        for (int i = 0; i < selectedValues.length; i++) {
            if (selectedValues[i].equals("1")) {
                values.put("1", 1f);
            } else {

                values.put(chineseToPinyin(selectedValues[i]), genericRow.getValues().get(selectedValues[i]));
            }
        }
        values.put("timestamp", genericRow.getValues().get("timestamp"));
        GenericRow newRow = new GenericRow(values);
        printHelper("after", "select", newRow, null);
        return newRow;


    }
    private String chineseToPinyin(String source) {
        char[] cs = source.toCharArray();
        StringBuilder sb = new StringBuilder();
        boolean hanyu = false;
        for (char c : cs) {
            if (c <= 128) {
                sb.append(c);
                hanyu = false;
            } else {
                String[] pinyins = PinyinHelper.toHanyuPinyinStringArray(c);
                String pinyin = pinyins[0].substring(0, pinyins[0].length() - 1);
                System.out.println(pinyin);
                if (hanyu)
                    sb.append('-');
                sb.append(pinyin);
                hanyu = true;
            }
        }
        return sb.toString();
    }

    public void printHelper(String aspect, String op, GenericRow left, GenericRow right) {
        if (right != null) {
            System.out.println(String.format("record %s %s : left(value) : {%s}\n right(agg): {%s} \n",
                    aspect, op, left.toString(), right.toString()));
        } else {
            System.out.println(String.format("record %s %s : {%s} \n", aspect, op, left.toString()));
        }


    }
}
