package com.uade.structure.implementation;


import com.uade.structure.definition.SetADT;

import java.util.Random;

public class DynamicSet implements SetADT {

    // Nodo interno
    private class Node {
        int data;
        Node next;

        Node(int data) {
            this.data = data;
            this.next = null;
        }
    }

    private Node head;
    private int size;
    private Random random;

    public DynamicSet() {
        head = null;
        size = 0;
        random = new Random();
    }

    @Override
    public boolean exist(int value) {
        Node current = head;
        while (current != null) {
            if (current.data == value) {
                return true;
            }
            current = current.next;
        }
        return false;
    }

    @Override
    public int choose() {
        if (isEmpty()) {
            throw new IllegalStateException("El conjunto está vacío");
        }

        // Generar un índice aleatorio entre 0 (inclusive) y size (exclusive)
        int index = (int) (Math.random() * size);

        Node current = head;
        for (int i = 0; i < index; i++) {
            current = current.next;
        }
        return current.data;
    }

    @Override
    public void add(int value) {
        if (exist(value)) {
            return; // No se agregan duplicados
        }

        Node newNode = new Node(value);
        newNode.next = head;
        head = newNode;
        size++;
    }

    @Override
    public void remove(int element) {
        if (isEmpty()) return;

        if (head.data == element) {
            head = head.next;
            size--;
            return;
        }

        Node current = head;
        while (current.next != null) {
            if (current.next.data == element) {
                current.next = current.next.next;
                size--;
                return;
            }
            current = current.next;
        }
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }
}