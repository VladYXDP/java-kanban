package manager;

import task.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CustomLinkedList<E extends Task> {

    private final Map<Integer, Node<E>> taskHashTable = new HashMap<>();
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
        if (head == null) {
            head = new Node<>(task, null, null);
            tail = head;
        } else if (tail == head) {
            if (taskHashTable.containsKey(task.getId())) {
                taskHashTable.clear();
                head = null;
                linkTask(task);
            } else {
                tail = new Node<>(task, null, head);
            }
        } else {
            Node<E> oldTail = tail;
            tail = new Node<>(task, null, oldTail);
            if (taskHashTable.containsKey(task.getId())) {
                removeNode(taskHashTable.get(task.getId()));
            }
        }
        taskHashTable.put(task.getId(), tail);
    }

    public void removeNode(Node<E> removingNode) {
        Node<E> next = removingNode.next;
        Node<E> prev = removingNode.prev;
        prev.next = next;
        next.prev = prev;
        taskHashTable.remove(removingNode.task.getId());
    }

    public List<Task> getTasks() {
        List<Task> tasks = new ArrayList<>();
        Node<E> node = head;
        while (node != null) {
            tasks.add(node.task);
            node = node.next;
        }
        return tasks;
    }

    public Map<Integer, Node<E>> getTaskHashTable() {
        return taskHashTable;
    }

    public Node<E> getHead() {
        return head;
    }

    public void setHead(Node<E> head) {
        this.head = head;
    }

    public Node<E> getTail() {
        return tail;
    }

    public void setTail(Node<E> tail) {
        this.tail = tail;
    }
}
