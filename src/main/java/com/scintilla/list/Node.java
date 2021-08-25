package com.scintilla.list;

/**
 * Doubly-linked list node.
 *
 * @author Shukai Z
 * @param <E> the type of elements held in doubly-linked list.
 */
public class Node<E> {

    /**
     * Values contained in the node.
     */
    E item;

    /**
     * The next node of the current node.
     */
    Node<E> next;

    /**
     * The previous node of the current node.
     */
    Node<E> prev;

    /**
     * Constructs a doubly-linked list.
     *
     * @param element current node
     */
    public Node(E element) {
        this.item = element;
    }

    /**
     * Constructs a doubly-linked list.
     *
     * @param prev previous node
     * @param element current node
     * @param next next node
     */
    public Node(Node<E> prev, E element, Node<E> next) {
        this.item = element;
        this.next = next;
        this.prev = prev;
    }
}
