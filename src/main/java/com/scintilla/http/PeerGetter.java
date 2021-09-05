package com.scintilla.http;

/**
 * PeerGetter is the interface that must be implemented by a peer.
 * HTTP client for accessing remote nodes.
 */
public interface PeerGetter {
    /**
     * Find cached values from the group
     *
     * @param groupName group name.
     * @param key cache key.
     * @return cache value.
     */
    byte[] get(String groupName, String key);
}
