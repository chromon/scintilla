package com.scintilla.cache;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Doubly linked list node item.
 */
public class Entry<T> {

    /**
     * cache map key.
     */
    private String key;

    /**
     * entry value.
     */
    private T value;

    /**
     * entry value length.
     */
    private long valueLength;

    /**
     * Constructs a entry object with key and value,
     * and calculate the value length.
     *
     * @param key entry key.
     * @param value entry value.
     */
    public Entry(String key, T value) {
        this.key = key;
        this.value = value;
        this.valueLength = calValueLength();
    }

    /**
     * convert object to json string and get the string length.
     * @return object length.
     */
    public long calValueLength() {
        ObjectMapper objectMapper = new ObjectMapper();
        String str = null;
        try {
            str = objectMapper.writeValueAsString(this.value);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return str.length();
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }

    public long getValueLength() {
        return valueLength;
    }

    public void setValueLength(long valueLength) {
        this.valueLength = valueLength;
    }
}
