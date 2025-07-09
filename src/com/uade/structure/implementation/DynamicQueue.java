package com.uade.structure.implementation;


import com.uade.structure.definition.QueueADT;

public class DynamicQueue implements QueueADT {

    // Clase interna: nodo de la cola
    private class Node {
        int data;
        Node next;

        Node(int data) {
            this.data = data;
            this.next = null;
        }
    }

    private Node front; // apunta al primer elemento (para eliminar)
    private Node rear;  // apunta al último elemento (para agregar)

    public DynamicQueue() {
        front = null;
        rear = null;
    }

    @Override
    public int getElement() {
        if (isEmpty()) {
            throw new IllegalStateException("La cola está vacía");
        }
        return front.data;
    }

    @Override
    public void add(int value) {
        Node newNode = new Node(value);
        if (isEmpty()) {
            front = rear = newNode; // el primer nodo es tanto frente como final
        } else {
            rear.next = newNode;
            rear = newNode;
        }
    }

    @Override
    public void remove() {
        if (isEmpty()) {
            throw new IllegalStateException("La cola está vacía");
        }
        front = front.next;
        if (front == null) {
            rear = null; // si la cola quedó vacía
        }
    }

    @Override
    public boolean isEmpty() {
        return front == null;
    }
}