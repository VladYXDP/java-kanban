package manager.task;

import manager.Managers;
import manager.history.HistoryManager;
import task.Epic;
import task.Subtask;
import task.Task;
import task.TaskStatus;

import java.util.*;

public class InMemoryTaskManager implements TaskManager {

    protected final Map<Integer, Task> tasks = new HashMap<>();
    protected final Map<Integer, Epic> epics = new HashMap<>();
    protected final Map<Integer, Subtask> subtasks = new HashMap<>();
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
        prioritizedTasks.removeAll(tasks.values());
        tasks.clear();
    }

    @Override
    public void removeAllEpic() {
        for (Epic epic : epics.values()) {
            historyManager.remove(epic.getId());
        }
        removeAllSubtask();
        epics.clear();
    }

    @Override
    public void removeAllSubtask() {
        for (Epic epic : epics.values()) {
            epic.removeAllSubtask();
        }
        for (Subtask subtask : subtasks.values()) {
            historyManager.remove(subtask.getId());
        }
        prioritizedTasks.removeAll(subtasks.values());
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
        if (task != null && !tasks.containsKey(task.getId()) && validateStartTime(task)) {
            task.setStatus(TaskStatus.NEW);
            task.setId(++taskIndex);
            tasks.put(task.getId(), task);
            prioritizedTasks.add(task);
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
        if (subtask != null && !subtasks.containsKey(subtask.getId()) && validateStartTime(subtask)) {
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
        if (task != null) {
            Task oldTask = tasks.get(task.getId());
            prioritizedTasks.remove(getTask(task.getId()));
            if (validateStartTime(task)) {
                tasks.put(task.getId(), task);
                prioritizedTasks.add(task);
            } else {
                prioritizedTasks.add(oldTask);
            }
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
        if (subtask != null) {
            Subtask oldSubtask = subtasks.get(subtask.getId());
            prioritizedTasks.remove(getSubtask(subtask.getId()));
            if (epics.containsKey(subtask.getEpicId()) && validateStartTime(subtask)) {
                subtasks.put(subtask.getId(), subtask);
                epics.get(subtask.getEpicId()).changeSubtask(subtask);
                prioritizedTasks.add(subtask);
            } else {
                prioritizedTasks.add(oldSubtask);
            }
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

    private boolean validateStartTime(Task task) {
        List<Task> prioritizedTaskList = getPrioritizedTasks();
        if (prioritizedTaskList.size() > 1) {
            if (task.getEndTime().isBefore(prioritizedTaskList.get(0).getStartTime())
                    || task.getStartTime().isAfter(prioritizedTaskList.get(prioritizedTaskList.size() - 1).getEndTime())) {
                return true;
            }
            for (int i = 0; i < prioritizedTaskList.size() - 1; i++) {
                if (task.getStartTime().isAfter(prioritizedTaskList.get(i).getEndTime())
                        && task.getEndTime().isBefore(prioritizedTaskList.get(i + 1).getStartTime())) {
                    return true;
                }
            }
        } else if (prioritizedTaskList.size() == 1) {
            return task.getStartTime().isAfter(prioritizedTaskList.get(0).getEndTime())
                    || task.getEndTime().isBefore(prioritizedTaskList.get(0).getStartTime());
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
