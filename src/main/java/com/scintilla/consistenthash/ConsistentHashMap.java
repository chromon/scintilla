package com.scintilla.consistenthash;

import com.scintilla.consistenthash.hash.DefaultHash;
import com.scintilla.consistenthash.hash.Hash;

import java.util.*;

/**
 * Consistent hash map provides an implementation of a ring hash.
 */
public class ConsistentHashMap {

    /**
     * Hash function.
     */
    private Hash hash;

    /**
     * Virtual node multiples.
     */
    private int replicas;

    /**
     * A ring hash.
     */
    public List<Integer> keys;

    /**
     * The mapping of virtual nodes to real nodes.
     * The key is the hash of the virtual node
     * and the value is the name of the real node.
     */
    private Map<Integer, String> hashMap;

    /**
     * Constructs consistent hash map with replicas
     *
     * @param replicas Virtual node multiples.
     */
    public ConsistentHashMap(int replicas) {
        this.replicas = replicas;
        this.hashMap = new HashMap<>();
        this.hash = new DefaultHash();
        this.keys = new ArrayList<>();
    }

    /**
     * Constructs consistent hash map with replicas and hash func.
     *
     * @param replicas Virtual node multiples.
     * @param hash Hash function.
     */
    public ConsistentHashMap(int replicas, Hash hash) {
        this.hash = hash;
        this.replicas = replicas;
        this.hashMap = new HashMap<>();
        this.keys = new ArrayList<>();

        if (this.hash == null) {
            this.hash = new DefaultHash();
        }
    }

    /**
     * Adds some real node keys to the hash.
     *
     * @param keys Real nodes key.
     */
    public void add(String... keys) {
        for (String key : keys) {
            for (int i = 0; i < this.replicas; i++) {
                // virtual node hash.
                int keyHash = this.hash.hash((i + key).getBytes());
                this.keys.add(keyHash);
                // Add the mapping relationship between virtual and real nodes.
                this.hashMap.put(keyHash, key);
            }
        }
        Collections.sort(this.keys);
    }

    /**
     * Gets the closest item(nodes) in the hash to the provided key.
     *
     * @param key Cache key.
     * @return Node key.
     */
    public String get(String key) {
        if (this.keys.size() == 0) {
            return "";
        }

        int hash = this.hash.hash(key.getBytes());
        int idx = this.searchInsert(this.keys, hash);

        return this.hashMap.get(this.keys.get(idx % this.keys.size()));
    }

    /**
     * Search insert position with binary search.
     *
     * @param nums list.
     * @param target key.
     * @return index.
     */
    public int searchInsert(List<Integer> nums, int target) {
        int n = nums.size();
        int left = 0, right = n - 1, ans = n;

        while (left <= right) {
            int mid = ((right - left) >> 1) + left;
            if (target <= nums.get(mid)) {
                ans = mid;
                right = mid - 1;
            } else {
                left = mid + 1;
            }
        }
        return ans;
    }
}
