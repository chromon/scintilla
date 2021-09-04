package com.scintilla.consistenthash.hash;

/**
 * Customized Hash interface.
 */
public interface Hash {

    /**
     * Hash maps bytes to int
     *
     * @param data hash data.
     * @return hash value.
     */
    int hash(byte[] data);
}
