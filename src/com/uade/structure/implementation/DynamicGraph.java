package com.uade.structure.implementation;

import com.uade.structure.definition.GraphADT;
import com.uade.structure.definition.SetADT;

public class DynamicGraph implements GraphADT {

    private class Edge {
        int destination;
        int weight;
        Edge next;

        Edge(int destination, int weight) {
            this.destination = destination;
            this.weight = weight;
        }
    }

    private class Vertex {
        int value;
        Edge edges;
        Vertex next;

        Vertex(int value) {
            this.value = value;
        }
    }

    private Vertex head;

    public DynamicGraph() {
        this.head = null;
    }

    @Override
    public SetADT getVertxs() {
        SetADT set = new DynamicSet();
        Vertex current = head;
        while (current != null) {
            set.add(current.value);
            current = current.next;
        }
        return set;
    }

    @Override
    public void addVertx(int vertex) {
        Vertex current = head;
        while (current != null) {
            if (current.value == vertex) return; // ya existe
            current = current.next;
        }
        Vertex newVertex = new Vertex(vertex);
        newVertex.next = head;
        head = newVertex;
    }

    @Override
    public void removeVertx(int vertex) {
        Vertex current = head;
        while (current != null) {
            removeEdge(current.value, vertex);
            current = current.next;
        }

        if (head != null && head.value == vertex) {
            head = head.next;
            return;
        }

        current = head;
        while (current != null && current.next != null) {
            if (current.next.value == vertex) {
                current.next = current.next.next;
                return;
            }
            current = current.next;
        }
    }

    @Override
    public void addEdge(int vertxOne, int vertxTwo, int weight) {
        Vertex v1 = null, v2 = null;
        Vertex current = head;
        while (current != null) {
            if (current.value == vertxOne) v1 = current;
            if (current.value == vertxTwo) v2 = current;
            current = current.next;
        }

        if (v1 == null || v2 == null) return;

        // Agregar arista v1 -> v2
        Edge edge = v1.edges;
        while (edge != null) {
            if (edge.destination == vertxTwo) {
                edge.weight = weight;
                break;
            }
            edge = edge.next;
        }
        if (edge == null) {
            Edge newEdge = new Edge(vertxTwo, weight);
            newEdge.next = v1.edges;
            v1.edges = newEdge;
        }

        // Agregar arista v2 -> v1 (grafo no dirigido)
        edge = v2.edges;
        while (edge != null) {
            if (edge.destination == vertxOne) {
                edge.weight = weight;
                break;
            }
            edge = edge.next;
        }
        if (edge == null) {
            Edge newEdge = new Edge(vertxOne, weight);
            newEdge.next = v2.edges;
            v2.edges = newEdge;
        }
    }

    @Override
    public void removeEdge(int vertxOne, int vertxTwo) {
        Vertex current = head;
        Vertex v1 = null, v2 = null;

        while (current != null) {
            if (current.value == vertxOne) v1 = current;
            if (current.value == vertxTwo) v2 = current;
            current = current.next;
        }

        if (v1 != null) {
            if (v1.edges != null && v1.edges.destination == vertxTwo) {
                v1.edges = v1.edges.next;
            } else {
                Edge e = v1.edges;
                while (e != null && e.next != null) {
                    if (e.next.destination == vertxTwo) {
                        e.next = e.next.next;
                        break;
                    }
                    e = e.next;
                }
            }
        }

        if (v2 != null) {
            if (v2.edges != null && v2.edges.destination == vertxOne) {
                v2.edges = v2.edges.next;
            } else {
                Edge e = v2.edges;
                while (e != null && e.next != null) {
                    if (e.next.destination == vertxOne) {
                        e.next = e.next.next;
                        break;
                    }
                    e = e.next;
                }
            }
        }
    }

    @Override
    public boolean existsEdge(int vertxOne, int vertxTwo) {
        Vertex current = head;
        while (current != null) {
            if (current.value == vertxOne) {
                Edge e = current.edges;
                while (e != null) {
                    if (e.destination == vertxTwo) return true;
                    e = e.next;
                }
                return false;
            }
            current = current.next;
        }
        return false;
    }

    @Override
    public int edgeWeight(int vertxOne, int vertxTwo) {
        Vertex current = head;
        while (current != null) {
            if (current.value == vertxOne) {
                Edge e = current.edges;
                while (e != null) {
                    if (e.destination == vertxTwo) {
                        return e.weight;
                    }
                    e = e.next;
                }
                throw new IllegalArgumentException("No existe la arista");
            }
            current = current.next;
        }
        throw new IllegalArgumentException("Vértice no encontrado");
    }

    @Override
    public boolean isEmpty() {
        return head == null;
    }
}
