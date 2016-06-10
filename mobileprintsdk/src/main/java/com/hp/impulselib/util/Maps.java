package com.hp.impulselib.util;

import java.util.HashMap;
import java.util.Map;

public class Maps {
    /** Return the inverse of a map, that is, each unique value is mapped to its former key. */
    public static <K, V> Map<V, K> inverse(Map<K, V> original) {
        Map<V, K> newMap = new HashMap<>();
        for (Map.Entry<K, V> entry: original.entrySet()) {
            newMap.put(entry.getValue(), entry.getKey());
        }
        return newMap;
    }
}
