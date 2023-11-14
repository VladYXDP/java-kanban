package manager.history;

import task.Task;

import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {

    private final CustomLinkedList<Task> customLinkedList = new CustomLinkedList<>();

    @Override
    public void addTask(Task task) {
        customLinkedList.linkTask(task);
    }

    @Override
    public List<Task> getHistory() {
        return customLinkedList.getTasks();
    }

    @Override
    public void remove(int id) {
        customLinkedList.remove(id);
    }
}
