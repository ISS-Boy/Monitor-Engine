package cn.issboy.mengine.core.analyzer;

import cn.issboy.mengine.core.metastore.MetaStore;
import cn.issboy.mengine.core.parser.BlockGroup;
import cn.issboy.mengine.core.parser.BlockValues;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicInteger;

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
            // 拷贝一份,在analyze的时候会变更旧视图(加Filed)
            MetaStore tmpMetaStore = metaStore.clone();

            ExecutorService analyzeExecutors = Executors.newWorkStealingPool(Runtime.getRuntime().availableProcessors());

            Analyzer analyzer = new Analyzer(analysis,tmpMetaStore);
            analyzer.process(block);
            analysisGroup.add(analysis);
        }
        return analysisGroup;
    }
}
