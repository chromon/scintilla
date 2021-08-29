package com.scintilla.cache;

import org.junit.Assert;
import org.junit.Test;

public class EntryTest {

    @Test
    public void testEntry() {
        // Entry<String> e1 = new Entry<>("", "abcd");
        Entry<Integer> e2 = new Entry<>("", 123);
        Entry<Boolean> e3 = new Entry<>("", true);
        Assert.assertEquals(4, e3.getValueLength());
    }
}
