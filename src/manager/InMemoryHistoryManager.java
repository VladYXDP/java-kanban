package manager;

import task.Task;

import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {

    private final List<Task> history = new ArrayList<>();
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
        for (Task task : history) {
            if (task.getId() == id) {
                history.remove(task);
            }
        }
    }
}
