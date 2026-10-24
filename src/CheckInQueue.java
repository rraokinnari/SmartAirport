import java.util.ArrayList;
import java.util.List;

/**
 * FIFO queue (linked list) that simulates the check-in counter line.
 * enqueue/dequeue/peek are all O(1).
 */
public class CheckInQueue {

    private static class Node {
        final Passenger data;
        Node next;
        Node(Passenger data) { this.data = data; }
    }

    private Node head; // front of the line (served first)
    private Node tail; // back of the line (newest arrival)
    private int size;

    /** A passenger joins the back of the line. */
    public void enqueue(Passenger p) {
        Node node = new Node(p);
        if (tail == null) {
            head = tail = node;
        } else {
            tail.next = node;
            tail = node;
        }
        size++;
    }

    /** Serves the passenger at the front. Returns null if the queue is empty. */
    public Passenger dequeue() {
        if (head == null) return null;
        Passenger p = head.data;
        head = head.next;
        if (head == null) tail = null;
        size--;
        return p;
    }

    public Passenger peek() {
        return head == null ? null : head.data;
    }

    /** Puts a passenger back at the FRONT (only used by Undo). */
    public void addFront(Passenger p) {
        Node node = new Node(p);
        node.next = head;
        head = node;
        if (tail == null) tail = node;
        size++;
    }

    /** Removes a passenger from anywhere in the line. */
    public boolean removeById(int id) {
        Node prev = null;
        Node cur = head;
        while (cur != null) {
            if (cur.data.getId() == id) {
                if (prev == null) head = cur.next;
                else prev.next = cur.next;
                if (cur == tail) tail = prev;
                size--;
                return true;
            }
            prev = cur;
            cur = cur.next;
        }
        return false;
    }

    public boolean contains(int id) {
        for (Node cur = head; cur != null; cur = cur.next) {
            if (cur.data.getId() == id) return true;
        }
        return false;
    }

    /** Snapshot of the line from front to back. */
    public List<Passenger> toList() {
        List<Passenger> list = new ArrayList<>();
        for (Node cur = head; cur != null; cur = cur.next) list.add(cur.data);
        return list;
    }

    public int size() { return size; }
    public boolean isEmpty() { return size == 0; }
}
