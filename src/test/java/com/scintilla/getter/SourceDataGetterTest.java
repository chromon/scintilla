package com.scintilla.getter;

import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;

public class SourceDataGetterTest {

    @Test
    public void testGet() {
        Assert.assertEquals(true, Arrays.equals("abc".getBytes(), new SourceDataGetter() {
            @Override
            public byte[] getSourceData(String key) {
                return key.getBytes();
            }
        }.getSourceData("abc")));

    }
}
