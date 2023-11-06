package manager;

import task.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CustomLinkedList<E extends Task> {

    private final Map<Integer, Node<E>> tasks = new HashMap<>();
    private Node<E> head;
    private Node<E> tail;

    public CustomLinkedList() {
    }

    private static class Node<T extends Task> {
        T task;
        Node<T> next;
        Node<T> prev;

        public Node(T task, Node<T> next, Node<T> prev) {
            this.task = task;
            this.next = next;
            this.prev = prev;
        }
    }

    public void linkTask(E task) {
        if (tasks.containsKey(task.getId())) {
            if (tail.task.equals(task)) {
                return;
            }
            removeNode(tasks.get(task.getId()));
        }
        if (head == null) {
            head = new Node<>(task, null, null);
            tail = head;
        } else if (tail == head) {
            tail = new Node<>(task, null, head);
            head.next = tail;
        } else {
            Node<E> oldTail = tail;
            tail = new Node<>(task, null, oldTail);
            oldTail.next = tail;
        }
        tasks.put(task.getId(), tail);
    }

    public void removeNode(Node<E> removingNode) {
        if (head == removingNode) {
            if (head == tail) {
                head = null;
                tail = null;
            } else if (head.next == tail) {
                tail.prev = null;
                head = tail;
            } else {
                head = head.next;
                head.prev = null;
            }
        } else if (tail == removingNode) {
            if (tail.prev == head) {
                head.next = null;
                tail = head;
            } else {
                tail = tail.prev;
                tail.next = null;
            }
        } else {
            Node<E> prev = removingNode.prev;
            removingNode.prev.next = removingNode.next;
            removingNode.next.prev = prev;
        }
        tasks.remove(removingNode.task.getId());
    }

    public void remove(int id) {
        if (tasks.containsKey(id)) {
            removeNode(tasks.get(id));
        }
    }

    public List<Task> getTasks() {
        List<Task> history = new ArrayList<>();
        Node<E> node = head;
        while (node != null) {
            history.add(node.task);
            node = node.next;
        }
        return history;
    }
}
