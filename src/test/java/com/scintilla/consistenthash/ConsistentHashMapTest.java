package com.scintilla.consistenthash;

import com.scintilla.consistenthash.hash.Hash;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public class ConsistentHashMapTest {

    @Test
    public void testAdd() {
        ConsistentHashMap hashMap = new ConsistentHashMap(3, new Hash() {
            @Override
            public int hash(byte[] data) {
                return Integer.parseInt(new String(data));
            }
        });

        hashMap.add("6", "4", "2");

        for (int i : hashMap.keys) {
            System.out.println(i);
        }
    }

    @Test
    public void testHash() {
        ConsistentHashMap hashMap = new ConsistentHashMap(3, new Hash() {
            @Override
            public int hash(byte[] data) {
                return Integer.parseInt(new String(data));
            }
        });

        hashMap.add("6", "4", "2");

        Map<String, String> testCase = new HashMap<>();
        testCase.put("2", "2");
        testCase.put("11", "2");
        testCase.put("23", "4");
        testCase.put("27", "2");

        for (Map.Entry<String, String> entry : testCase.entrySet()) {
            if (!hashMap.get(entry.getKey()).equals(entry.getValue())) {
                System.err.println("asking for " + entry.getKey()
                        + ", should have yielded " + entry.getValue());
            }
        }

        hashMap.add("8");

        testCase.put("27", "8");

        for (Map.Entry<String, String> entry : testCase.entrySet()) {
            if (!hashMap.get(entry.getKey()).equals(entry.getValue())) {
                System.err.println("asking for " + entry.getKey()
                        + ", should have yielded " + entry.getValue());
            }
        }
    }
}
