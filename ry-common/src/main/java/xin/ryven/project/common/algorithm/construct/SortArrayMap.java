package xin.ryven.project.common.algorithm.construct;

import lombok.AllArgsConstructor;
import lombok.ToString;

import java.util.Arrays;

/**
 * Function:根据 key 排序的 Map
 *
 * @author crossoverJie
 * Date: 2019-02-25 18:17
 * @since JDK 1.8
 */
public class SortArrayMap {

    private Node[] buckets;

    private static final int DEFAULT_CAPACITY = 1 << 4;

    private int size = 0;

    public SortArrayMap() {
        this.buckets = new Node[DEFAULT_CAPACITY];
    }

    public void add(Long key, String value) {
        checkSize(size + 1);
        Node node = new Node(key, value);
        buckets[size++] = node;
    }

    private void checkSize(int size) {
        if (size >= buckets.length) {
            int oldLen = buckets.length;
            int newLen = oldLen + (oldLen >> 1);
            buckets = Arrays.copyOf(buckets, newLen);
        }
    }

    public void sort() {
        Arrays.sort(buckets, 0, size, (o1, o2) -> o1.key > o2.key ? 1 : (o1.key.equals(o2.key) ? 0 : -1));
    }

    public String getFirstNode(long key) {
        if (size == 0) {
            return null;
        }
        for (Node node: buckets) {
            if (node == null) {
                continue;
            }
            if (node.key > key) {
                return node.value;
            }
        }
        return buckets[0].value;
    }

    @ToString
    @AllArgsConstructor
    private class Node {
        Long key;
        String value;
    }
}
