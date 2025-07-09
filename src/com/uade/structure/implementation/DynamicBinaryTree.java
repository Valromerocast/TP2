package com.uade.structure.implementation;


import com.uade.structure.definition.BinaryTreeADT;

public class DynamicBinaryTree implements BinaryTreeADT {
    private class Node {
        int data;
        Node left;
        Node right;

        Node(int data) {
            this.data = data;
        }
    }

    private Node root;

    public DynamicBinaryTree() {
        root = null;
    }

    @Override
    public int getRoot() {
        if (root == null) {
            throw new IllegalStateException("El árbol está vacío");
        }
        return root.data;
    }

    @Override
    public BinaryTreeADT getLeft() {
        if (root == null || root.left == null) {
            return null;
        }
        DynamicBinaryTree leftTree = new DynamicBinaryTree();
        leftTree.root = root.left;
        return leftTree;
    }

    @Override
    public BinaryTreeADT getRight() {
        if (root == null || root.right == null) {
            return null;
        }
        DynamicBinaryTree rightTree = new DynamicBinaryTree();
        rightTree.root = root.right;
        return rightTree;
    }

    @Override
    public void add(int value) {
        if (root == null) {
            root = new Node(value);
            return;
        }

        Node current = root;
        while (true) {
            if (value < current.data) {
                if (current.left == null) {
                    current.left = new Node(value);
                    return;
                }
                current = current.left;
            } else if (value > current.data) {
                if (current.right == null) {
                    current.right = new Node(value);
                    return;
                }
                current = current.right;
            } else {
                return; // No se permiten duplicados
            }
        }
    }

    @Override
    public void remove(int value) {
        if (root == null) {
            throw new IllegalStateException("El árbol está vacío");
        }

        Node parent = null;
        Node current = root;

        // Buscar el nodo a eliminar
        while (current != null && current.data != value) {
            parent = current;
            if (value < current.data) {
                current = current.left;
            } else {
                current = current.right;
            }
        }

        if (current == null) {
            throw new IllegalArgumentException("El valor no existe en el árbol");
        }

        // Caso 1: sin hijos
        if (current.left == null && current.right == null) {
            if (parent == null) {
                root = null;
            } else if (parent.left == current) {
                parent.left = null;
            } else {
                parent.right = null;
            }
        }

        // Caso 2: un hijo
        else if (current.left == null || current.right == null) {
            Node child = (current.left != null) ? current.left : current.right;

            if (parent == null) {
                root = child;
            } else if (parent.left == current) {
                parent.left = child;
            } else {
                parent.right = child;
            }
        }

        // Caso 3: dos hijos
        else {
            Node successorParent = current;
            Node successor = current.right;
            while (successor.left != null) {
                successorParent = successor;
                successor = successor.left;
            }

            current.data = successor.data;

            if (successorParent.left == successor) {
                successorParent.left = successor.right;
            } else {
                successorParent.right = successor.right;
            }
        }
    }

    @Override
    public boolean isEmpty() {
        return root == null;
    }
}
