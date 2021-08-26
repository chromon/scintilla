package com.scintilla.list;

import java.util.NoSuchElementException;

/**
 * Doubly-Linked list.
 *
 * @author Shukai Z
 * @param <E> the type of elements held in this collection
 */
public class DoublyLinkedList<E> {

    /**
     * Number of elements in the list.
     */
    private int size = 0;

    /**
     * Pointer to first node.
     */
    private Node<E> first;

    /**
     * Pointer to last node.
     */
    private Node<E> last;

    /**
     * Constructs an empty list.
     */
    public DoublyLinkedList() {}

    /**
     * Appends the specified element to the end of this list.
     *
     * @param e element to be appended to this list
     * @return {@code true}
     */
    public boolean add(E e) {
        final Node<E> l = this.last;
        final Node<E> newNode = new Node<>(l, e, null);
        this.last = newNode;

        if (l == null) {
            this.first = newNode;
        } else {
            l.next = newNode;
        }

        this.size++;

        return true;
    }

    /**
     * Appends the specified node to the end of this list.
     *
     * @param node node to be appended to this list
     * @return {@code true}
     */
    public boolean add(Node<E> node) {
        final Node<E> l = this.last;
        node.prev = l;
        this.last = node;

        if (l == null) {
            this.first = node;
        } else {
            l.next = node;
        }

        this.size++;

        return true;
    }

    /**
     * Removes and returns the first element from this list.
     *
     * @return the first element from this list
     * @throws NoSuchElementException if this list is empty
     */
    public E removeFirst() {
        final Node<E> f = first;
        if (f == null) {
            throw new NoSuchElementException();
        }

        final E element = f.item;
        final Node<E> nextNode = f.next;
        f.item = null;
        f.next = null;
        this.first = nextNode;

        if (nextNode == null) {
            this.last = null;
        } else {
            nextNode.prev = null;
        }

        this.size--;

        return element;
    }

    /**
     * Removes and returns the last element from this list.
     *
     * @return the last element from this list
     * @throws NoSuchElementException if this list is empty
     */
    public E removeLast() {
        final Node<E> l = last;
        if (l == null) {
            throw new NoSuchElementException();
        }

        final E element = l.item;
        final Node<E> prevNode = l.prev;
        l.item = null;
        l.prev = null;
        this.last = prevNode;

        if (prevNode == null) {
            this.first = null;
        } else {
            prevNode.next = null;
        }

        this.size--;

        return element;
    }

    /**
     * returns the last node from this list, but not delete it.
     *
     * @return the last node
     */
    public Node<E> last() {
        return this.last;
    }

    /**
     * returns the first node from this list, but not delete it.
     *
     * @return the first node
     */
    public Node<E> first() {
        return this.first;
    }

    /**
     * Moves element e to the front of list (right of the list)
     *
     * @param node moved node
     * @throws NoSuchElementException if this node is null
     */
    public void moveToFront(Node<E> node) {
        if (node == null) {
            throw new NoSuchElementException();
        }

        if (this.last != node) {
            if (node.prev == null) {
                node.next.prev = null;
                this.first = node.next;
            } else {
                node.prev.next = node.next;
                node.next.prev = node.prev;
            }

            final Node<E> l = this.last;
            l.next = node;
            node.prev = l;
            this.last = node;
        }
    }

    /**
     * Returns the number of elements in this list.
     *
     * @return the number of elements in this list
     */
    public int size() {
        return this.size;
    }

    /**
     * Traversing the list.
     */
    public void traverse() {
        Node<E> node = this.first;
        boolean flag = false;
        while (node != null) {
            if (node == this.last) {
                flag = true;
            }

            System.out.print(node.item + " ");
            node = node.next;

            if (flag) {
                break;
            }
        }
        System.out.println();
    }
}
