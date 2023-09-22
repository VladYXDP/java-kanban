package manager;

import task.Epic;
import task.Subtask;
import task.Task;
import task.TaskStatus;

import java.util.ArrayList;
import java.util.HashMap;

public class Manager {

    private HashMap<Integer, Task> tasks = new HashMap<>();
    private HashMap<Integer, Epic> epics = new HashMap<>();
    private HashMap<Integer, Subtask> subtasks = new HashMap<>();

    private int taskIndex = 0;

    public ArrayList<Task> getAllTask() {
        return new ArrayList<>(tasks.values());
    }

    public ArrayList<Epic> getAllEpic() {
        return new ArrayList<>(epics.values());
    }

    public ArrayList<Subtask> getAllSubtask() {
        return new ArrayList<>(subtasks.values());
    }

    public void removeAllTask() {
        tasks.clear();
    }

    public void removeAllEpic() {
        epics.clear();
        subtasks.clear();
    }

    public void removeAllSubtask() {
        for (Epic epic : epics.values()) {
            epic.removeAllSubtask();
        }
        subtasks.clear();
    }

    public Task getTask(int id) {
        return tasks.get(id);
    }

    public Epic getEpic(int id) {
        return epics.get(id);
    }

    public Subtask getSubtask(int id) {
        return subtasks.get(id);
    }

    public void createTask(Task task) {
        if (task != null && !tasks.containsKey(task.getId())) {
            task.setStatus(TaskStatus.NEW);
            task.setId(++taskIndex);
            tasks.put(task.getId(), task);
        }
    }

    public void createEpic(Epic epic) {
        if (epic != null && !epics.containsKey(epic.getId())) {
            epic.setStatus(TaskStatus.NEW);
            epic.setId(++taskIndex);
            epics.put(epic.getId(), epic);
        }
    }

    public void createSubtask(Subtask subtask) {
        if (subtask != null && !subtasks.containsKey(subtask.getId())) {
            if (epics.containsKey(subtask.getEpicId())) {
                epics.get(subtask.getEpicId()).addSubtask(subtask);
                subtask.setId(++taskIndex);
                subtasks.put(subtask.getId(), subtask);
            }
        }
    }

    public void updateTask(Task task) {
        if (task != null) {
            tasks.put(task.getId(), task);
        }
    }

    //Тут несовсем понял. Сделал так
    public void updateEpic(Epic epic) {
        if (epic != null) {
            Epic oldEpic = epics.get(epic.getId());
            if (oldEpic.equals(epic)) {
                epics.put(epic.getId(), epic);
            } else {
                System.out.println("Epic не изменился!");
            }
        }
    }

    public void updateSubtask(Subtask subtask) {
        if (subtask != null && epics.containsKey(subtask.getEpicId())) {
            subtasks.put(subtask.getId(), subtask);
            Epic epic = epics.get(subtask.getEpicId());
            for (Subtask tmpSubtask : epic.getSubtasks()) {
                if (subtask.getId() == tmpSubtask.getId()) {
                    epic.changeSubtask(subtask, tmpSubtask);
                    break;
                }
            }
        }
    }

    public void removeTaskById(int id) {
        tasks.remove(id);
    }

    public void removeEpicById(int id) {
        if (epics.containsKey(id)) {
            Epic epic = epics.get(id);
            epics.remove(id);
            for (Subtask tmpSubtask : epic.getSubtasks()) {
                removeSubtaskById(tmpSubtask.getId());
            }
        }
    }

    public void removeSubtaskById(int id) {
        if (subtasks.containsKey(id)) {
            Subtask subtask = subtasks.get(id);
            subtasks.remove(id);
            if (epics.containsKey(subtask.getEpicId())) {
                epics.get(subtask.getEpicId()).removeSubtask(subtask);
            }
        }
    }

    public ArrayList<Subtask> getSubtaskByEpic(Epic epic) {
        return epic.getSubtasks();
    }
}
