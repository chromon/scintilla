package com.scintilla.consistenthash.hash;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Arrays;

/**
 * Default Hash implementation.
 */
public class DefaultHash implements Hash {

    @Override
    public int hash(byte[] data) {
        return Arrays.hashCode(data) & Integer.MAX_VALUE;
    }
}
