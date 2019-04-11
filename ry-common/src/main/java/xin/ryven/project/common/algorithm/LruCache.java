package xin.ryven.project.common.algorithm;

import java.util.HashMap;
import java.util.Map;

/**
 * 临时缓存用户
 * lru
 *
 * @author gray
 */
public class LruCache<K, V> {

    private Entry<K, V> head, tail;

    private Map<K, Entry<K, V>> map;

    private int capacity;

    public LruCache(int capacity) {
        this.map = new HashMap<>(capacity);
        this.capacity = capacity;
        this.head = new Entry<>(null, null);
        this.tail = new Entry<>(null, null);
        this.head.next = this.tail;
        this.tail.pre = this.head;
    }

    public void put(K k, V v) {
        if (map.containsKey(k)) {
            Entry<K, V> entry = map.get(k);
            entry.value = v;
            popToTail(entry);
        } else {
            if (map.size() >= this.capacity) {
                delFirst();
            }
            Entry<K, V> entry = new Entry<>(k, v);
            addTail(entry);
            this.map.put(k, entry);
        }
    }

    public V get(K k) {
        Entry<K, V> kvEntry = this.map.get(k);
        if (kvEntry == null) {
            return null;
        }
        popToTail(kvEntry);
        return kvEntry.value;
    }

    private void delFirst() {
        Entry<K, V> first = this.head.next;
        this.head.next = first.next;
        first.next.pre = this.head;
        //删除Map元素
        this.map.remove(first.key);
    }

    private void popToTail(Entry<K, V> entry) {
        entry.next.pre = entry.pre;
        entry.pre.next = entry.next;
        //Tail
        this.tail.pre.next = entry;
        entry.pre = this.tail.pre;
        entry.next = this.tail;
        this.tail.pre = entry;
    }

    private void addTail(Entry<K, V> entry) {
        entry.pre = this.tail.pre;
        entry.next = this.tail;
        this.tail.pre.next = entry;
        this.tail.pre = entry;
    }

    private class Entry<Key, Value> {
        Key key;
        Value value;
        Entry<K, V> next;
        Entry<K, V> pre;

        Entry(Key key, Value value) {
            this.key = key;
            this.value = value;
        }
    }

}
