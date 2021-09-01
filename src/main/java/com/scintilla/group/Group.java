package com.scintilla.group;

import com.scintilla.Cache;
import com.scintilla.getter.Getter;
import com.scintilla.view.ByteView;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A Group is a cache namespace and associated data loaded spread over.
 */
public class Group {

    /**
     * Group name as a cache namespace, each Group has a unique name.
     */
    private String name;

    /**
     * Callback interface for getting source data when the cache is not hit.
     */
    private Getter getter;

    /**
     * Cache with concurrency features.
     */
    private Cache cache;

    /**
     * Constructs a Group and put it in Groups map.
     *
     * @param name Group name.
     * @param cacheBytes Cache size.
     * @param getter Getter callback func.
     * @param groups Group collection.
     */
    public Group(String name, long cacheBytes, Groups groups, Getter getter) {
        if (getter == null) {
            throw new RuntimeException("Getter is null.");
        }

        this.name = name;
        this.cache = new Cache(cacheBytes);
        this.getter = getter;

        groups.getGroups().put(name, this);
    }

    /**
     * Get value for a key from cache.
     *
     * @param key Cache key.
     * @return Byte view.
     */
    public ByteView get(String key) {
        Logger logger = Logger.getLogger(Group.class.getName());

        if (key.equals("")) {
            logger.log(Level.WARNING, "key is required.");
            return null;
        }

        // Get cache data from Cache, if exists return the data.
        ByteView byteView = this.cache.get(key);
        if (byteView != null) {
            logger.log(Level.INFO, "[Cache] hit");
            return byteView;
        }

        // If not exists invoke load method.
        return this.load(key);
    }

    /**
     * Loads key either by invoking the getter locally or by sending it to another machine.
     */
    public ByteView load(String key) {
        return this.getLocally(key);
    }

    /**
     * Loads key by invoking the getter locally
     *
     * @param key Cache key.
     * @return Byte view.
     */
    private ByteView getLocally(String key) {
        // Get the source data, customized by the user.
        byte[] bytes = this.getter.get(key);
        if (bytes == null) {
            return null;
        }

        // Encapsulated Data for ByteView.
        ByteView value = new ByteView();
        value.setB(value.cloneBytes(bytes));
        // Add source data to cache.
        this.populateCache(key, value);

        return value;
    }

    /**
     * Add source data to cache.
     *
     * @param key Cache key.
     * @param value Cache value.
     */
    private void populateCache(String key, ByteView value) {
        this.cache.add(key, value);
    }
}
