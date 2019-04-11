package xin.ryven.project.common.algorithm.impl.consistenthash;

import java.util.SortedMap;
import java.util.TreeMap;
import java.util.stream.IntStream;

/**
 * Function:TreeMap 实现
 *
 * @author crossoverJie
 * Date: 2019-02-27 01:16
 * @since JDK 1.8
 */
public class TreeMapConsistentHash extends AbstractConsistentHash {

    private TreeMap<Long, String> treeMap = new TreeMap<>();
    /**
     * 虚拟节点数量
     */
    private static final int VIRTUAL_NODE_SIZE = 2;

    @Override
    protected void add(long key, String value) {
        IntStream.range(0, VIRTUAL_NODE_SIZE).forEach(i -> treeMap.put(hash("VIRTUAL" + key + i), value));
        treeMap.put(key, value);
    }

    @Override
    protected String getFirst(String value) {
        SortedMap<Long, String> tailMap = treeMap.tailMap(hash(value));
        if (tailMap.isEmpty()) {
            return treeMap.firstEntry().getValue();
        } else {
            return tailMap.get(tailMap.firstKey());
        }
    }
}
