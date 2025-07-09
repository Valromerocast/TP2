package com.uade.structure.implementation;


import com.uade.structure.definition.MultipleDictionaryADT;
import com.uade.structure.definition.SetADT;

public class MultipleDictionary implements MultipleDictionaryADT {

    private class Entry {
        int key;
        DynamicSet values; // conjunto de valores asociados a la clave
        Entry next;

        Entry(int key) {
            this.key = key;
            this.values = new DynamicSet();
            this.next = null;
        }
    }

    private Entry head;
    private int size;

    public MultipleDictionary() {
        head = null;
        size = 0;
    }

    @Override
    public void add(int key, int value) {
        Entry current = head;

        // Buscar si ya existe la clave
        while (current != null) {
            if (current.key == key) {
                current.values.add(value); // agregamos valor al conjunto existente
                return;
            }
            current = current.next;
        }

        // Si no existe la clave, crear nueva entrada
        Entry newEntry = new Entry(key);
        newEntry.values.add(value);
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
    public void remove(int key, int value) {
        Entry current = head;
        while (current != null) {
            if (current.key == key) {
                current.values.remove(value);
                // Si el conjunto queda vacío, eliminar la entrada
                if (current.values.isEmpty()) {
                    remove(key);
                }
                return;
            }
            current = current.next;
        }
    }

    @Override
    public SetADT get(int key) {
        Entry current = head;
        while (current != null) {
            if (current.key == key) {
                // Devolvemos el conjunto
                return current.values;
            }
            current = current.next;
        }
        throw new IllegalArgumentException("Clave no existe en el diccionario");
    }

    @Override
    public SetADT getKeys() {
        DynamicSet keys = new DynamicSet();
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