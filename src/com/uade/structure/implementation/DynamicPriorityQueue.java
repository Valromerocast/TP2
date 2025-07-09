package com.uade.structure.implementation;


import com.uade.structure.definition.PriorityQueueADT;

public class DynamicPriorityQueue implements PriorityQueueADT {

    // Nodo interno con valor y prioridad
    private class Node {
        int data;
        int priority;
        Node next;

        Node(int data, int priority) {
            this.data = data;
            this.priority = priority;
            this.next = null;
        }
    }

    private Node front; // nodo con mayor prioridad (menor valor numérico)

    public DynamicPriorityQueue() {
        front = null;
    }

    @Override
    public int getElement() {
        if (isEmpty()) {
            throw new IllegalStateException("La cola está vacía");
        }
        return front.data;
    }

    @Override
    public int getPriority() {
        if (isEmpty()) {
            throw new IllegalStateException("La cola está vacía");
        }
        return front.priority;
    }

    @Override
    public void add(int value, int priority) {
        Node newNode = new Node(value, priority);

        // Caso 1: la cola está vacía o el nuevo tiene mayor prioridad que el front
        if (front == null || priority < front.priority) {
            newNode.next = front;
            front = newNode;
        } else {
            // Insertar en la posición correcta
            Node current = front;
            while (current.next != null && current.next.priority <= priority) {
                current = current.next;
            }
            newNode.next = current.next;
            current.next = newNode;
        }
    }

    @Override
    public void remove() {
        if (isEmpty()) {
            throw new IllegalStateException("La cola está vacía");
        }
        front = front.next;
    }

    @Override
    public boolean isEmpty() {
        return front == null;
    }
}
