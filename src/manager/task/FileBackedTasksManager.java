package manager.task;

import task.Epic;
import task.Subtask;
import task.Task;
import task.TaskStatus;

import java.io.*;
import java.nio.charset.StandardCharsets;

import static manager.task.TaskType.TASK;

public class FileBackedTasksManager extends InMemoryTaskManager {

    private File file;

    public FileBackedTasksManager(File file) {
        this.file = file;
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
        try (BufferedWriter bf = new BufferedWriter(new FileWriter(file.getName(), StandardCharsets.UTF_8, true))) {

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void loadFromFile(File file) {
        try (BufferedReader br = new BufferedReader(new FileReader(file, StandardCharsets.UTF_8))) {
            br.readLine();
            while (br.ready()) {
                createTaskFromString(br.readLine());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static Task createTaskFromString(String taskStr) {
        String[] taskStrSplit = taskStr.split(",");
        int id = Integer.parseInt(taskStrSplit[0]);
        String name = taskStrSplit[2];
        TaskStatus status = TaskStatus.getTaskStatusByString(taskStrSplit[3]);
        String desc = taskStrSplit[4];
        if (TASK.name().equals(taskStrSplit[1])) {
            new Task(name, desc, id, status);
        } else if (TaskType.EPIC.name().equals(taskStrSplit[1])) {
            new Epic(name, desc, id, status);
        }
        return new Subtask(name, desc, id, status, Integer.parseInt(taskStrSplit[5]));
    }
}