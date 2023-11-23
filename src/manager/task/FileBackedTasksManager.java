package manager.task;

import manager.ManagerLoadException;
import manager.ManagerSaveException;
import task.Epic;
import task.Subtask;
import task.Task;
import task.TaskStatus;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static manager.task.TaskType.TASK;

public class FileBackedTasksManager extends InMemoryTaskManager {

    private final File file;

    private final List<String> loadedStringTasks;

    public static void main(String[] args) {
        FileBackedTasksManager fileBackedTasksManager = loadFromFile(new File("tasks.csv"));
        fileBackedTasksManager.createTaskFromString();
    }

    public FileBackedTasksManager(File file, List<String> loadedStringTasks) {
        this.file = file;
        this.loadedStringTasks = loadedStringTasks;
    }

    @Override
    public void removeAllTask() {
        super.removeAllTask();
        save();
    }

    @Override
    public void removeAllEpic() {
        super.removeAllEpic();
        save();
    }

    @Override
    public void removeAllSubtask() {
        super.removeAllSubtask();
        save();
    }

    @Override
    public void createTask(Task task) {
        super.createTask(task);
        save();
    }

    @Override
    public void createEpic(Epic epic) {
        super.createEpic(epic);
        save();
    }

    @Override
    public void createSubtask(Subtask subtask) {
        super.createSubtask(subtask);
        save();
    }

    @Override
    public Task getTask(int id) {
        Task task = super.getTask(id);
        save();
        return task;
    }

    @Override
    public Epic getEpic(int id) {
        Epic epic = super.getEpic(id);;
        save();
        return epic;
    }

    @Override
    public Subtask getSubtask(int id) {
        Subtask subtask = super.getSubtask(id);
        save();
        return subtask;
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        super.updateSubtask(subtask);
        save();
    }

    @Override
    public void removeTaskById(int id) {
        super.removeTaskById(id);
        save();
    }

    @Override
    public void removeEpicById(int id) {
        super.removeEpicById(id);
        save();
    }

    @Override
    public void removeSubtaskById(int id) {
        super.removeSubtaskById(id);
        save();
    }

    private void save() {
        if (file != null) {
            try (BufferedWriter bw = new BufferedWriter(new FileWriter(file.getName(), StandardCharsets.UTF_8, false))) {
                bw.write("id,type,name,status,description,epic");
                bw.newLine();
                for (Task task : getAllTask()) {
                    bw.write(task.taskToString());
                    bw.newLine();
                }
                for (Epic epic : getAllEpic()) {
                    bw.write(epic.taskToString());
                    bw.newLine();
                }
                for (Subtask subtask : getAllSubtask()) {
                    bw.write(subtask.taskToString());
                    bw.newLine();
                }
                bw.newLine();
                if (!getHistory().isEmpty()) {
                    bw.write(historyToString(getHistory()));
                }
            } catch (IOException e) {
                throw new ManagerSaveException(e.getMessage());
            }
        }
    }

    public static FileBackedTasksManager loadFromFile(File file) {
        List<String> loadedStringTasks = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(file, StandardCharsets.UTF_8))) {
            br.readLine();
            while (br.ready()) {
                String line = br.readLine();
                if (!line.isBlank()) {
                    loadedStringTasks.add(line);
                }
            }
        } catch (IOException e) {
            throw new ManagerLoadException(e.getMessage());
        }
        return new FileBackedTasksManager(file, loadedStringTasks);
    }

    private void createTaskFromString() {
        for (int i = 0; i < loadedStringTasks.size() - 1; i++) {
            String[] taskStrSplit = loadedStringTasks.get(i).split(",");
            int id = Integer.parseInt(taskStrSplit[0]);
            String name = taskStrSplit[2];
            TaskStatus status = TaskStatus.getTaskStatusByString(taskStrSplit[3]);
            String desc = taskStrSplit[4];
            if (TASK.name().equals(taskStrSplit[1])) {
                addTask(new Task(name, desc, id, status));
            } else if (TaskType.EPIC.name().equals(taskStrSplit[1])) {
                addEpic(new Epic(name, desc, id, status));
            } else {
                addSubtask(new Subtask(name, desc, id, status, Integer.parseInt(taskStrSplit[5])));
            }
        }
        loadHistory(historyFromString(loadedStringTasks));
    }

    private static String historyToString(List<Task> historyTasks) {
        StringBuilder sb = new StringBuilder();
        for (Task task : historyTasks) {
            sb.append(task.getId()).append(",");
        }
        sb.delete(sb.length() - 1, sb.length());
        return sb.toString();
    }

    private static List<Integer> historyFromString(List<String> loadedStringTasks) {
        String[] historyStr = loadedStringTasks.get(loadedStringTasks.size() - 1).split(",");
        List<Integer> historyIdList = new ArrayList<>(historyStr.length);
        for (String history : historyStr) {
            historyIdList.add(Integer.parseInt(history));
        }
        return historyIdList;
    }

    private void loadHistory(List<Integer> historyIdList) {
        for (Integer historyId : historyIdList) {
            if (tasks.containsKey(historyId)) {
                historyManager.addTask(tasks.get(historyId));
            } else if (epics.containsKey(historyId)) {
                historyManager.addTask(epics.get(historyId));
            } else if (subtasks.containsKey(historyId)) {
                historyManager.addTask(subtasks.get(historyId));
            }
        }
    }

    private void addTask(Task task) {
        if (!tasks.containsKey(task.getId())) {
            tasks.put(task.getId(), task);
            taskIndex = task.getId();
        }
    }

    private void addEpic(Epic epic) {
        if (!epics.containsKey(epic.getId())) {
            epics.put(epic.getId(), epic);
            taskIndex = epic.getId();
        }
    }

    private void addSubtask(Subtask subtask) {
        if (!subtasks.containsKey(subtask.getId())) {
            subtasks.put(subtask.getId(), subtask);
            taskIndex = subtask.getId();
            epics.get(subtask.getEpicId()).getSubtasks().add(subtask);
        }
    }
}