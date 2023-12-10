package manager.task;

import manager.Managers;
import manager.history.HistoryManager;
import task.Epic;
import task.Subtask;
import task.Task;
import task.TaskStatus;

import java.time.LocalDateTime;
import java.util.*;

public class InMemoryTaskManager implements TaskManager {

    protected final HashMap<Integer, Task> tasks = new HashMap<>();
    protected final HashMap<Integer, Epic> epics = new HashMap<>();
    protected final HashMap<Integer, Subtask> subtasks = new HashMap<>();
    protected final HistoryManager historyManager = Managers.getDefaultHistory();
    protected final Set<Task> prioritizedTasks = new TreeSet<>(new TaskComparatorByStartTime());

    protected int taskIndex = 0;

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
        prioritizedTasks.removeAll(tasks.values());
    }

    @Override
    public void removeAllEpic() {
        for (Epic epic : epics.values()) {
            historyManager.remove(epic.getId());
        }
        removeAllSubtask();
        epics.clear();
        prioritizedTasks.removeAll(epics.values());
        prioritizedTasks.removeAll(subtasks.values());
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
        prioritizedTasks.removeAll(subtasks.values());
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
        if (task != null && !tasks.containsKey(task.getId()) && validateStartTime(task.getStartTime())) {
            task.setStatus(TaskStatus.NEW);
            task.setId(++taskIndex);
            tasks.put(task.getId(), task);
            prioritizedTasks.add(task);
        }
    }

    @Override
    public void createEpic(Epic epic) {
        if (epic != null && !epics.containsKey(epic.getId()) && validateStartTime(epic.getStartTime())) {
            epic.setStatus(TaskStatus.NEW);
            epic.setId(++taskIndex);
            epics.put(epic.getId(), epic);
            prioritizedTasks.add(epic);
        }
    }

    @Override
    public void createSubtask(Subtask subtask) {
        if (subtask != null && !subtasks.containsKey(subtask.getId()) && validateStartTime(subtask.getStartTime())) {
            if (epics.containsKey(subtask.getEpicId())) {
                epics.get(subtask.getEpicId()).addSubtask(subtask);
                subtask.setId(++taskIndex);
                subtasks.put(subtask.getId(), subtask);
                prioritizedTasks.add(subtask);
            } else {
                throw new RuntimeException("Отсутствует Эпик для подзадачи.");
            }
        }
    }

    @Override
    public void updateTask(Task task) {
        if (task != null && validateStartTime(task.getStartTime())) {
            tasks.put(task.getId(), task);
            prioritizedTasks.add(task);
        }
    }

    @Override
    public void updateEpic(Epic epic) {
        if (epic != null && validateStartTime(epic.getStartTime())) {
            Epic oldEpic = epics.get(epic.getId());
            if (oldEpic.equals(epic)) {
                oldEpic.setName(epic.getName());
                oldEpic.setDescription(epic.getDescription());
            }
            prioritizedTasks.add(epic);
        }
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        if (subtask != null && epics.containsKey(subtask.getEpicId()) && validateStartTime(subtask.getStartTime())) {
            subtasks.put(subtask.getId(), subtask);
            epics.get(subtask.getEpicId()).changeSubtask(subtask);
            prioritizedTasks.add(subtask);
        }
    }

    @Override
    public void removeTaskById(int id) {
        prioritizedTasks.remove(tasks.remove(id));
        historyManager.remove(id);
    }

    @Override
    public void removeEpicById(int id) {
        if (epics.containsKey(id)) {
            Epic epic = epics.get(id);
            prioritizedTasks.remove(epics.remove(id));
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
            prioritizedTasks.remove(subtasks.remove(id));
            if (epics.containsKey(subtask.getEpicId())) {
                epics.get(subtask.getEpicId()).removeSubtask(subtask);
            }
            historyManager.remove(id);
        }
    }

    @Override
    public List<Subtask> getSubtaskByEpic(Epic epic) {
        return epic.getSubtasks();
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    @Override
    public List<Task> getPrioritizedTasks() {
        return new ArrayList<>(prioritizedTasks);
    }

    //Если новая задача по времени начала ее выполнения раньше чем начало последней задачи в приоритетных, то возвращаем false
    private boolean validateStartTime(LocalDateTime stateTime) {
        if(getPrioritizedTasks().get(getPrioritizedTasks().size()).getStartTime().isAfter(stateTime)){
            return false;
        }
        return true;
    }

    public static class TaskComparatorByStartTime implements Comparator<Task> {
        @Override
        public int compare(Task o1, Task o2) {
            if (o1.getStartTime().isBefore(o2.getStartTime())) {
                return -1;
            } else if (o1.getStartTime().equals(o2.getStartTime()))
                return 0;
            return 1;
        }
    }
}
