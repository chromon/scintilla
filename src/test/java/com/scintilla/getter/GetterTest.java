package com.scintilla.getter;

import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;

public class GetterTest {

    @Test
    public void testGet() {
        Assert.assertEquals(true, Arrays.equals("abc".getBytes(), new Getter() {
            @Override
            public byte[] get(String key) {
                return key.getBytes();
            }
        }.get("abc")));

    }
}
