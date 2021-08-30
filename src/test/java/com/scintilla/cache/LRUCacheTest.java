package com.scintilla.cache;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class LRUCacheTest {

    private LRUCache cache;
    private LRUCache cache2;

    @Before
    public void init() {
        cache = new LRUCache(24);
        cache.add("123", 123);
        cache.add("s1", "abc");
        cache.add("bool", true);

        cache2 = new LRUCache(24, new Callback() {
            @Override
            public <T> void process(String key, T value) {
                System.out.println(key + "," + value);
            }
        });
        cache2.add("123", 123);
        cache2.add("s1", "abc");
        cache2.add("bool", true);
    }

    @Test
    public void testAdd() {

        Assert.assertEquals(21, cache.getnBytes());

        cache.add("1", 123456);
        Assert.assertEquals(22, cache.getnBytes());
        Assert.assertEquals("abc", cache.getQueue().getFirst().getItem().getValue());
    }

    @Test
    public void testGet() {
        Assert.assertEquals(123, ((Integer) cache.get("123")).intValue());
        Assert.assertEquals(123, cache.getQueue().getLast().getItem().getValue());

        Assert.assertEquals("abc", (String) cache.get("s1"));
        Assert.assertEquals("abc", cache.getQueue().getLast().getItem().getValue());
    }

    @Test
    public void testRemove() {
        cache.remove();
        Assert.assertEquals("abc", cache.getQueue().getFirst().getItem().getValue());
    }

    @Test
    public void testCallback() {
        Assert.assertEquals(21, cache2.getnBytes());

        cache2.add("1", 123456);
        Assert.assertEquals(22, cache2.getnBytes());
        Assert.assertEquals("abc", cache2.getQueue().getFirst().getItem().getValue());
    }
}
