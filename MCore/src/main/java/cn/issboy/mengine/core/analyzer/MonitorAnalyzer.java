package cn.issboy.mengine.core.analyzer;

import cn.issboy.mengine.core.metastore.MetaStore;
import cn.issboy.mengine.core.parser.BlockGroup;
import cn.issboy.mengine.core.parser.BlockValues;

import java.util.ArrayList;
import java.util.List;

/**
 * created by just on 18-4-23
 */
public class MonitorAnalyzer {

    private final MetaStore metaStore;

    public MonitorAnalyzer(MetaStore metaStore) {
        this.metaStore = metaStore;
    }

    public List<Analysis> analyze(BlockGroup blockGroup) {

        List<Analysis> analysisGroup= new ArrayList<>();
        List<BlockValues> blockValues = blockGroup.getBlockValues();

        for (BlockValues block : blockValues) {
            Analysis analysis = new Analysis();

            Analyzer analyzer = new Analyzer(analysis,metaStore);
            analyzer.process(block);
            analysisGroup.add(analysis);
        }
        return analysisGroup;
    }
}
