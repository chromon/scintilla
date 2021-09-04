package com.scintilla.consistenthash.hash;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

/**
 * Default Hash implementation.
 */
public class DefaultHash implements Hash {

    @Override
    public int hash(byte[] data) {
        Object key = this.toObject(data);
        int h = 0;
        return (key == null) ? 0 : (h = key.hashCode()) ^ (h >>> 16);
    }

    /**
     * Convert byte array to object.
     *
     * @param bytes byte array.
     * @return object.
     */
    public Object toObject(byte[] bytes) {
        Object obj = null;
        try {
            ByteArrayInputStream bis = new ByteArrayInputStream (bytes);
            ObjectInputStream ois = new ObjectInputStream (bis);
            obj = ois.readObject();
            ois.close();
            bis.close();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return obj;
    }
}
