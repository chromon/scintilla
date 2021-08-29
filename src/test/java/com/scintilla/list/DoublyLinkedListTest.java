package com.scintilla.list;

import com.scintilla.cache.Entry;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class DoublyLinkedListTest {

    private DoublyLinkedList list;
    private Node n2;

    @Before
    public void init() {
        Entry<String> e1 = new Entry<>("s1", "abc");
        Entry<Integer> e2 = new Entry<>("i1", 123);
        Entry<Boolean> e3 = new Entry<>("b3", true);

        Node n1 = new Node(e1);
        n2 = new Node(e2);
        Node n3 = new Node(e3);

        list = new DoublyLinkedList();
        list.add(n1);
        list.add(n2);
        list.add(n3);
    }

    @Test
    public void testAdd() {
        Assert.assertEquals("abc", list.getFirst().getItem().getValue());
        Assert.assertEquals(true, list.getLast().getItem().getValue());
    }

    @Test
    public void testRemoveLast() {
        list.removeLast();
        Assert.assertEquals(123, list.getLast().getItem().getValue());
    }


    @Test
    public void testRemoveFirst() {
        list.removeFirst();
        Assert.assertEquals(123, list.getFirst().getItem().getValue());
    }

    @Test
    public void testMoveToFront() {
        list.moveToFront(n2);
        Assert.assertEquals(123, list.getLast().getItem().getValue());
    }
}
