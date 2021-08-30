package com.scintilla.getter;

/**
 * A Getter loads data for a key.
 * If the cache data not exists, invoke the get method to get source data.
 */
public interface Getter {
    /**
     * Get returns the value identified by key, populating dest.
     *
     * @param key cache key.
     * @return data.
     */
    byte[] get(String key);
}