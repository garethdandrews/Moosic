package com.example.gareth.moosic;

/**
 * A node is one element of a linked list. It stores the corresponding song object and the
 * nodes before and after this one.
 */

class Node {
    Song song;
    Node next;
    Node prev;

    /**
     * Initialises the node
     * @param song the song to be stored
     */
    Node(Song song) {
        this.song = song;
    }

    /**
     * @return song corresponding to this node
     */
    Song getSong() {
        return song;
    }

    /**
     * @return the node next in the queue
     */
    Node getNext() {
        return next;
    }

    /**
     * @return the node previous in the queue
     */
    Node getPrev() {
        return prev;
    }

    /**
     * Inserts a node after this node
     * @param n the song that is currently playing
     */
    void upNext(Node n) {
        Node temp = this.next;
        this.next = n;
        n.prev = this;
        n.next = temp;
        temp.prev = n;
    }
}
