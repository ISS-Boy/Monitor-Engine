package cn.issboy.mengine.core.util;

import java.io.Serializable;

/**
 * 元组 : K-V对
 * created by just on 18-5-17
 */
public class Pair<K,V> implements Serializable{

    private K key;

    private V value;

    public K getKey() {
        return key;
    }

    public void setKey(K key) {
        this.key = key;
    }

    public V getValue() {
        return value;
    }

    public void setValue(V value) {
        this.value = value;
    }

    public Pair(K key, V value) {
        this.key = key;
        this.value = value;
    }

    @Override
    public String toString() {
        return key + " : " + value.toString();
    }
}
