package com.scintilla.list;

import com.scintilla.cache.Entry;

import java.util.NoSuchElementException;

/**
 * Doubly-Linked list.
 *
 * @author Shukai Z
 */
public class DoublyLinkedList {

    /**
     * Number of elements in the list.
     */
    private int size = 0;

    /**
     * Pointer to first node.
     */
    private Node first;

    /**
     * Pointer to last node.
     */
    private Node last;

    /**
     * Constructs an empty list.
     */
    public DoublyLinkedList() {}

    /**
     * Appends the specified node to the end of this list.
     *
     * @param node to be appended to this list
     * @return {@code true}
     */
    public boolean add(Node node) {
        final Node l = this.last;
        node.setPrev(l);
        this.last = node;

        if (l == null) {
            this.first = node;
        } else {
            l.setNext(node);
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
    public Entry<?> removeFirst() {
        final Node f = first;
        if (f == null) {
            throw new NoSuchElementException();
        }

        final Entry<?> element = f.getItem();
        final Node nextNode = f.getNext();
        f.setItem(null);
        f.setNext(null);
        this.first = nextNode;

        if (nextNode == null) {
            this.last = null;
        } else {
            nextNode.setPrev(null);
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
    public Entry<?> removeLast() {
        final Node l = last;
        if (l == null) {
            throw new NoSuchElementException();
        }

        final Entry<?> element = l.getItem();
        final Node prevNode = l.getPrev();
        l.setItem(null);
        l.setPrev(null);
        this.last = prevNode;

        if (prevNode == null) {
            this.first = null;
        } else {
            prevNode.setNext(null);
        }

        this.size--;

        return element;
    }

    /**
     * returns the last node from this list, but not delete it.
     *
     * @return the last node
     */
    public Node getLast() {
        return this.last;
    }

    /**
     * returns the first node from this list, but not delete it.
     *
     * @return the first node
     */
    public Node getFirst() {
        return this.first;
    }

    /**
     * Moves element e to the front of list (right of the list)
     *
     * @param node moved node
     * @throws NoSuchElementException if this node is null
     */
    public void moveToFront(Node node) {
        if (node == null) {
            throw new NoSuchElementException();
        }

        if (this.last != node) {
            if (node.getPrev() == null) {
                node.getNext().setPrev(null);
                this.first = node.getNext();
            } else {
                node.getPrev().setNext(node.getNext());
                node.getNext().setPrev(node.getPrev());
            }

            final Node l = this.last;
            l.setNext(node);
            node.setPrev(l);
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
        Node node = this.first;
        boolean flag = false;
        while (node != null) {
            if (node == this.last) {
                flag = true;
            }

            System.out.print(node.getItem() + " ");
            node = node.getNext();

            if (flag) {
                break;
            }
        }
        System.out.println();
    }
}
