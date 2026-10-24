import java.util.ArrayList;
import java.util.List;

/**
 * Generic LIFO stack built on a singly linked list.
 * push/pop/peek are all O(1).
 */
public class LinkedStack<T> {

    private static class Node<T> {
        final T data;
        final Node<T> next;
        Node(T data, Node<T> next) { this.data = data; this.next = next; }
    }

    private Node<T> top;
    private int size;

    public void push(T item) {
        top = new Node<>(item, top);
        size++;
    }

    /** Removes and returns the most recent item (null if empty). */
    public T pop() {
        if (top == null) return null;
        T item = top.data;
        top = top.next;
        size--;
        return item;
    }

    public T peek() {
        return top == null ? null : top.data;
    }

    /** Items from newest (top) to oldest. */
    public List<T> toList() {
        List<T> list = new ArrayList<>();
        for (Node<T> cur = top; cur != null; cur = cur.next) list.add(cur.data);
        return list;
    }

    public int size() { return size; }
    public boolean isEmpty() { return size == 0; }
}
