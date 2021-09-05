package com.scintilla.group;

import com.scintilla.getter.SourceDataGetter;
import com.scintilla.view.ByteView;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public class GroupTest {
    @Test
    public void testGet() {
        Map<String, String> db = new HashMap<>();
        db.put("a", "1");
        db.put("b", "2");
        db.put("c", "3");

        Map<String, Integer> loadCounts = new HashMap<>();
        Groups groups = new Groups();

        Group scores = new Group("scores", 2 << 10, groups, new SourceDataGetter() {
            @Override
            public byte[] getSourceData(String key) {
                System.out.println("[SlowDB] search key:" + key);
                String s = db.get(key);
                if (s != null) {
//                    if (loadCounts.get(key) == null) {
//                        loadCounts.put(key, 0);
//                    }
                    loadCounts.putIfAbsent(key, 0);
                    loadCounts.put(key, loadCounts.get(key) + 1);
                    return s.getBytes();
                }
                return null;
            }
        });

        for (Map.Entry<String, String> entry : db.entrySet()) {
            ByteView view = scores.get(entry.getKey());
            if (view == null || !view.toString().equals(entry.getValue())) {
                System.err.println("failed to get value.");
            }

            // second invoke
            view = scores.get(entry.getKey());
            if (view == null || loadCounts.get(entry.getKey()) > 1) {
                System.err.println("cache miss:" + entry.getKey());
            }
        }
    }
}
