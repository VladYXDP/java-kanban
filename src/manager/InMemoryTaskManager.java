package manager;

import task.Epic;
import task.Subtask;
import task.Task;
import task.TaskStatus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InMemoryTaskManager implements TaskManager {

    private final HashMap<Integer, Task> tasks = new HashMap<>();
    private final HashMap<Integer, Epic> epics = new HashMap<>();
    private final HashMap<Integer, Subtask> subtasks = new HashMap<>();
    private final HistoryManager historyManager = Managers.getDefaultHistory();

    private int taskIndex = 0;

    @Override
    public List<Task> getAllTask() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public List<Epic> getAllEpic() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public List<Subtask> getAllSubtask() {
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public void removeAllTask() {
        for (Task task : tasks.values()) {
            historyManager.remove(task.getId());
        }
        tasks.clear();
    }

    @Override
    public void removeAllEpic() {
        for (Epic epic : epics.values()) {
            historyManager.remove(epic.getId());
        }
        epics.clear();
        subtasks.clear();
    }

    @Override
    public void removeAllSubtask() {
        for (Epic epic : epics.values()) {
            epic.removeAllSubtask();
        }
        for (Subtask subtask : subtasks.values()) {
            historyManager.remove(subtask.getId());
        }
        subtasks.clear();
    }

    @Override
    public Task getTask(int id) {
        Task task = tasks.get(id);
        if (task != null) {
            historyManager.addTask(tasks.get(id));
        }
        return task;
    }

    @Override
    public Epic getEpic(int id) {
        Epic epic = epics.get(id);
        if (epic != null) {
            historyManager.addTask(epics.get(id));
        }
        return epic;
    }

    @Override
    public Subtask getSubtask(int id) {
        Subtask subtask = subtasks.get(id);
        if (subtask != null) {
            historyManager.addTask(subtasks.get(id));
        }
        return subtask;
    }

    @Override
    public void createTask(Task task) {
        if (task != null && !tasks.containsKey(task.getId())) {
            task.setStatus(TaskStatus.NEW);
            task.setId(++taskIndex);
            tasks.put(task.getId(), task);
        }
    }

    @Override
    public void createEpic(Epic epic) {
        if (epic != null && !epics.containsKey(epic.getId())) {
            epic.setStatus(TaskStatus.NEW);
            epic.setId(++taskIndex);
            epics.put(epic.getId(), epic);
        }
    }

    @Override
    public void createSubtask(Subtask subtask) {
        if (subtask != null && !subtasks.containsKey(subtask.getId())) {
            if (epics.containsKey(subtask.getEpicId())) {
                epics.get(subtask.getEpicId()).addSubtask(subtask);
                subtask.setId(++taskIndex);
                subtasks.put(subtask.getId(), subtask);
            }
        }
    }

    @Override
    public void updateTask(Task task) {
        if (task != null) {
            tasks.put(task.getId(), task);
        }
    }

    @Override
    public void updateEpic(Epic epic) {
        if (epic != null) {
            Epic oldEpic = epics.get(epic.getId());
            if (oldEpic.equals(epic)) {
                oldEpic.setName(epic.getName());
                oldEpic.setDescription(epic.getDescription());
            }
        }
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        if (subtask != null && epics.containsKey(subtask.getEpicId())) {
            subtasks.put(subtask.getId(), subtask);
            epics.get(subtask.getEpicId()).changeSubtask(subtask);
        }
    }

    @Override
    public void removeTaskById(int id) {
        tasks.remove(id);
        historyManager.remove(id);
    }

    @Override
    public void removeEpicById(int id) {
        if (epics.containsKey(id)) {
            Epic epic = epics.get(id);
            epics.remove(id);
            for (Subtask tmpSubtask : epic.getSubtasks()) {
                removeSubtaskById(tmpSubtask.getId());
            }
            historyManager.remove(id);
        }
    }

    @Override
    public void removeSubtaskById(int id) {
        if (subtasks.containsKey(id)) {
            Subtask subtask = subtasks.get(id);
            subtasks.remove(id);
            if (epics.containsKey(subtask.getEpicId())) {
                epics.get(subtask.getEpicId()).removeSubtask(subtask);
            }
            historyManager.remove(id);
        }
    }

    @Override
    public ArrayList<Subtask> getSubtaskByEpic(Epic epic) {
        return epic.getSubtasks();
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }
}
