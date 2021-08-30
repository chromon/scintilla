package com.scintilla;

import com.scintilla.cache.LRUCache;
import com.scintilla.view.ByteView;

/**
 * The LRUCache encapsulated Classes,
 * it's the external access interface, and implements concurrency features.
 */
public class Cache {

    /**
     * A real LRU cache.
     */
    private LRUCache lruCache;

    /**
     * The maximum size of the cache.
     */
    private long cacheBytes;

    /**
     * Constructs a Cache object with cacheBytes.
     *
     * @param cacheBytes The maximum size of the cache.
     */
    public Cache(long cacheBytes) {
        this.cacheBytes = cacheBytes;
    }

    /**
     * Add a value to the cache,
     * and adding concurrency features.
     *
     * @param key cache key.
     * @param value cache value.
     */
    public synchronized void add(String key, ByteView value) {
        if (this.lruCache == null) {
            // Lazy Initialization
            this.lruCache = new LRUCache(this.cacheBytes);
        }
        this.lruCache.add(key, value);
    }

    /**
     * Get look ups a key's value,
     * and adding concurrency features.
     *
     * @param key cache key.
     * @return cache value.
     */
    public synchronized ByteView get(String key) {
        if (this.lruCache == null) {
            return null;
        }

        Object obj = this.lruCache.get(key);
        if ( obj != null) {
            return (ByteView) obj;
        }
        return null;
    }
}
