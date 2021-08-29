package com.scintilla.cache;

import com.scintilla.list.DoublyLinkedList;
import com.scintilla.list.Node;

import java.util.HashMap;
import java.util.Map;

/**
 * LRUCache is a LRU cache.
 *
 * @author Shukai Z
 */
public class LRUCache {

    /**
     * Cache is a map with address of the corresponding queue node as value.
     */
    private Map<String, Node> cache;

    /**
     * Queue which is implemented using a doubly liked list.
     */
    private DoublyLinkedList queue;

    /**
     * The maximum size of the queue,
     * it will be equal to the maximum cache size.
     */
    private long maxBytes;

    /**
     * The cache size already used
     */
    private long nBytes;

    /**
     * Constructs a cache with maximum size.
     *
     * @param maxBytes the maximum size of the cache
     */
    public LRUCache(long maxBytes) {
        this.cache = new HashMap<>();
        this.queue = new DoublyLinkedList();
        this.maxBytes = maxBytes;
    }

    /**
     * Get look ups a key's value from the cache.
     *
     * @param key cache key.
     * @return the cache value.
     */
    public <T> T get(String key) {
        Node node = this.cache.get(key);
        if (node == null) {
            return null;
        }

        // move the node to last of list.
        this.queue.moveToFront(node);
        return (T) node.getItem().getValue();
    }

    /**
     * Removes the oldest element from cache
     * (and delete the last node in queue).
     */
    public void remove() {
        Node first = this.queue.getFirst();
        if (first != null) {
            Entry<?> entry = first.getItem();
            this.cache.remove(entry.getKey());

            // Nodes in the queue need to be deleted last
            this.queue.removeFirst();

            this.nBytes -= entry.getKey().length() + entry.getValueLength();
        }
    }

    /**
     * Adds a value to the cache, if the key exists, update it.
     *
     * @param key cache key.
     * @param value cache value.
     */
    public <T> void add(String key, T value) {
        Node node = this.cache.get(key);
        if (node != null) {
            this.queue.moveToFront(node);
            Entry<?> entry = node.getItem();
            Entry<?> newEntry = new Entry<>(key, value);
            this.nBytes += newEntry.getValueLength() - entry.getValueLength();
            node.setItem(newEntry);
        } else {
            Entry<T> entry = new Entry<>(key, value);
            Node newNode = new Node(entry);
            this.queue.add(newNode);
            this.cache.put(key, newNode);
            this.nBytes += key.length() + entry.getValueLength();
        }

        while (this.maxBytes != 0 && (this.maxBytes < this.nBytes)) {
            this.remove();
        }
    }

    /**
     * Length the number of cache entries
     *
     * @return cache entries count
     */
    public int size() {
        return this.queue.size();
    }

    public long getnBytes() {
        return nBytes;
    }

    public DoublyLinkedList getQueue() {
        return queue;
    }

    public long getMaxBytes() {
        return maxBytes;
    }
}
