package com.uade.structure.implementation;


import com.uade.structure.definition.SetADT;
import com.uade.structure.definition.SimpleDictionaryADT;

public class SimpleDictionary implements SimpleDictionaryADT {

    // Nodo interno
    private class Entry {
        int key;
        int value;
        Entry next;

        Entry(int key, int value) {
            this.key = key;
            this.value = value;
            this.next = null;
        }
    }

    private Entry head;
    private int size;

    public SimpleDictionary() {
        this.head = null;
        this.size = 0;
    }

    @Override
    public void add(int key, int value) {
        Entry current = head;

        // Buscar si ya existe la clave
        while (current != null) {
            if (current.key == key) {
                current.value = value; // Pisar el valor
                return;
            }
            current = current.next;
        }

        // Agregar al principio
        Entry newEntry = new Entry(key, value);
        newEntry.next = head;
        head = newEntry;
        size++;
    }

    @Override
    public void remove(int key) {
        if (isEmpty()) return;

        if (head.key == key) {
            head = head.next;
            size--;
            return;
        }

        Entry current = head;
        while (current.next != null) {
            if (current.next.key == key) {
                current.next = current.next.next;
                size--;
                return;
            }
            current = current.next;
        }
    }

    @Override
    public int get(int key) {
        Entry current = head;
        while (current != null) {
            if (current.key == key) {
                return current.value;
            }
            current = current.next;
        }
        throw new IllegalArgumentException("La clave no existe en el diccionario");
    }

    @Override
    public SetADT getKeys() {
        SetADT keys = new DynamicSet(); // Reutilizamos el conjunto dinámico

        Entry current = head;
        while (current != null) {
            keys.add(current.key);
            current = current.next;
        }

        return keys;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }
}