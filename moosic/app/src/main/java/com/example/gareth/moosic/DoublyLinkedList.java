package com.example.gareth.moosic;

/**
 * A data structure used to implement a queue feature. Doubly linked list means that you can
 * traverse the list both forwards and backwards, making it ideal for a queue. Next and previous
 * buttons also make use of this queue.
 */

class DoublyLinkedList {
    private Node head;
    private Node tail;
    private int size;

    /**
     * Initialises a queue
     */
    DoublyLinkedList() {
        this.size = 0;
    }

    /**
     * Returns true if nothing is in the queue
     * @return boolean
     */
    boolean isEmpty() {
        return size == 0;
    }

    /**
     * Adds a song to the start of queue
     * @param song to be added
     */
    void addStart(Song song) {
        Node n = new Node(song);
        if(isEmpty()){
            head = n;
            tail = n;
        }else{
            n.next = head;
            head.prev = n;
            head = n;
        }
        size++;
    }

    /**
     * Adds a song to the end of queue
     * @param song to be added
     */
    void addEnd(Song song) {
        Node n = new Node(song);
        if(isEmpty()){
            head = n;
            tail = n;
        }else{
            tail.next = n;
            n.prev = tail;
            tail = n;
        }
        size++;
    }

    /**
     * Deletes the list
     */
    void deleteList() {
        head = null;
        tail = null;
        size = 0;
    }

    /**
     * If repeat is on this function makes the list circular
     */
    void makeCircular() {
        head.prev = tail;
        tail.next = head;
    }

    /**
     * If repeat is off this function breaks the circle
     */
    void makeUncircular() {
        head.prev = null;
        tail.next = null;
    }

    /**
     * @return head of queue
     */
    Node getHead() {
        return head;
    }

    /**
     * @return tail of queue
     */
    Node getTail() {
        return tail;
    }
}