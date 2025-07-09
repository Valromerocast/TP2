package com.uade.structure.implementation;


import com.uade.structure.definition.StackADT;

public class DynamicStack implements StackADT {

    private class Node {
        int data;
        Node next;

        Node(int data) {
            this.data = data;
            this.next = null;
        }
    }

    private Node top;

    public DynamicStack() {
        this.top = null;
    }

    @Override
    public int getElement() {
        if (isEmpty()) {
            throw new IllegalStateException("La pila está vacía");
        }
        return top.data;
    }

    @Override
    public void add(int value) {
        Node newNode = new Node(value);
        newNode.next = top;
        top = newNode;
    }

    @Override
    public void remove() {
        if (isEmpty()) {
            throw new IllegalStateException("La pila está vacía");
        }
        top = top.next;
    }

    @Override
    public boolean isEmpty() {
        return top == null;
    }

}