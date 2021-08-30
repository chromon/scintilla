package com.scintilla.cache;

public interface Callback {
    <T> void process(String key, T value);
}
