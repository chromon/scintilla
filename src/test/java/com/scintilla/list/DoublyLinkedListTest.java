package com.scintilla.list;

import org.junit.Assert;
import org.junit.Test;

public class DoublyLinkedListTest {

    @Test
    public void testAdd() {
        DoublyLinkedList<String> list = new DoublyLinkedList<>();
        list.add("a");
        Node<String> node = new Node<>("b");
        list.add(node);
        list.add("c");

        Assert.assertEquals("a", list.first().item);
        Assert.assertEquals("c", list.last().item);
    }

    @Test
    public void testRemoveLast() {
        DoublyLinkedList<String> list = new DoublyLinkedList<>();
        list.add("a");
        Node<String> node = new Node<>("b");
        list.add(node);
        list.add("c");

        list.removeLast();

        Assert.assertEquals("b", list.last().item);
    }

    @Test
    public void testMoveToFront() {
        DoublyLinkedList<String> list = new DoublyLinkedList<>();
        list.add("a");
        Node<String> node = new Node<>("b");
        list.add(node);
        list.add("c");

        list.moveToFront(node);

        Assert.assertEquals("b", list.last().item);
    }
}
