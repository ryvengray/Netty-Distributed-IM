package xin.ryven.project.common.algorithm.impl.consistenthash;

import xin.ryven.project.common.algorithm.construct.SortArrayMap;

import java.util.stream.IntStream;

/**
 * @author gray
 */
public class SortArrayMapConsistentHash extends AbstractConsistentHash {

    private SortArrayMap sortArrayMap = new SortArrayMap();

    private static final int VIRTUAL_NODE = 2;

    @Override
    protected void add(long key, String value) {
        IntStream.range(0, VIRTUAL_NODE).forEach(i -> add(hash("VIRTUAL" + key + i), value));
        sortArrayMap.add(key, value);
    }

    @Override
    protected void sort() {
        sortArrayMap.sort();
    }

    @Override
    protected String getFirst(String value) {
        return sortArrayMap.getFirstNode(hash(value));
    }
}
