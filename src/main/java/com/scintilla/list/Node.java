package com.scintilla.list;

import com.scintilla.cache.Entry;

/**
 * Doubly-linked list node.
 *
 * @author Shukai Z
 */
public class Node {

    /**
     * Values contained in the node.
     */
    private Entry<?> item;

    /**
     * The next node of the current node.
     */
    private Node next;

    /**
     * The previous node of the current node.
     */
    private Node prev;

    /**
     * Constructs a doubly-linked list node with entry object.
     *
     * @param entry current node entry
     */
    public Node(Entry<?> entry) {
        this.item = entry;
    }

    public Entry<?> getItem() {
        return item;
    }

    public void setItem(Entry<?> item) {
        this.item = item;
    }

    public Node getNext() {
        return next;
    }

    public void setNext(Node next) {
        this.next = next;
    }

    public Node getPrev() {
        return prev;
    }

    public void setPrev(Node prev) {
        this.prev = prev;
    }
}
