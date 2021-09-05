package com.scintilla.getter;

/**
 * A SourceDataGetter loads data for a key.
 * If the cache data not exists, invoke the getSourceData method to get source data.
 */
public interface SourceDataGetter {
    /**
     * Get returns the value identified by key, populating dest.
     *
     * @param key cache key.
     * @return data.
     */
    byte[] getSourceData(String key);
}