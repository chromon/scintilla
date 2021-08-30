package com.scintilla.cache.callback;

/**
 * Callback interface, when invoke remove method to remove element from cache,
 * can implementation Callback interface.
 */
public interface Callback {
    <T> void process(String key, T value);
}
