package com.scintilla.http;

/**
 * PeerPicker is the interface that must be implemented to locate
 * the peer that owns a specific key.
 */
public interface PeerPicker {
    /**
     * Get the node PeerGetter based on the key.
     *
     * @param key cache key.
     * @return peer getter.
     */
    PeerGetter pickPeer(String key);
}
