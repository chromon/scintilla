package com.scintilla.view;

import java.util.Arrays;

/**
 * A ByteView holds an immutable view of bytes.
 *
 * @author Shukai Z
 */
public class ByteView {

    /**
     * Stores the real cached values.
     *
     * The byte type can support storage of all data types,
     * such as strings, images, etc.
     */
    private byte[] b;

    /**
     * Returns the view's length.
     * @return the view's length in memory.
     */
    public long len() {
        return b.length;
    }

    /**
     * Returns a copy of the data as a byte slice.
     * prevent cache values modified by external programs.
     *
     * @return  copy of the data
     */
    public byte[] byteSlice() {
        return this.b.clone();
    }

    /**
     * Returns a copy of the data.
     *
     * @param by data.
     * @return copy of the data.
     */
    public byte[] cloneBytes(byte[] by) {
        return by.clone();
    }

    /**
     * Returns the data as a string, making a copy if necessary.
     * @return the data as a string
     */
    @Override
    public String toString() {
        return new String(b);
    }

    public byte[] getB() {
        return b;
    }

    public void setB(byte[] b) {
        this.b = b;
    }
}
