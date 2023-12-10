package test;

import manager.task.TaskManager;
import task.Epic;
import task.Subtask;
import task.Task;

import java.util.List;

public abstract class TaskManagerTest<T extends TaskManager> {

    private T manager;

    private int taskIndex = 0;

    public List<Task> getAllTask() {
        return manager.getAllTask();
    }

    public List<Epic> getAllEpic() {
        return manager.getAllEpic();
    }

    public List<Subtask> getAllSubtask() {
        return manager.getAllSubtask();
    }

    public int addNewTask(Task task) {
        manager.createTask(task);
        return ++taskIndex;
    }

    public int addNewEpic(Epic epic) {
        manager.createEpic(epic);
        return ++taskIndex;
    }

    public int addNewSubtask(Subtask subtask) {
        manager.createSubtask(subtask);
        return ++taskIndex;
    }

    public Task getTask(int id) {
        return manager.getTask(id);
    }

    public Epic getEpic(int id) {
        return manager.getEpic(id);
    }

    public Subtask getSubtask(int id) {
        return manager.getSubtask(id);
    }

    public void removeAllTask() {
        manager.removeAllTask();
    }

    public void removeAllEpic() {
        manager.removeAllEpic();
    }

    public void removeAllSubtask() {
        manager.removeAllSubtask();
    }

    public void clearAll() {
        manager.removeAllTask();
        manager.removeAllEpic();
        manager.removeAllSubtask();
        taskIndex = 0;
    }

    public void updateTask(Task task) {
        manager.updateTask(task);
    }

    public void updateEpic(Epic epic) {
        manager.updateEpic(epic);
    }

    public void updateSubtask(Subtask subtask) {
        manager.updateSubtask(subtask);
    }

    public void removeTaskById(int id) {
        manager.removeTaskById(id);
    }

    public void removeEpicById(int id) {
        manager.removeEpicById(id);
    }

    public void removeSubtaskById(int id) {
        manager.removeSubtaskById(id);
    }

    public List<Subtask> getSubtaskByEpic(Epic epic) {
        return manager.getSubtaskByEpic(epic);
    }

    public void setManager(T manager) {
        this.manager = manager;
    }
}
