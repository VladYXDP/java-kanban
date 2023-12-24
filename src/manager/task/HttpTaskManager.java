package manager.task;

import client.KVTaskClient;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import gson.LocalDateTimeAdapter;
import task.Epic;
import task.Subtask;
import task.Task;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

public class HttpTaskManager extends FileBackedTasksManager {

    private KVTaskClient kvTaskClient;
    private Gson gson;

    public HttpTaskManager(String uri) throws IOException, InterruptedException {
        super("", null);
        kvTaskClient = new KVTaskClient(uri);
        gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .create();
    }

    private void addTask(List<Task> tasks) {
        for (Task task : tasks) {
            super.tasks.put(task.getId(), task);
        }
    }

    private void addEpic(List<Epic> epics) {
        for (Epic epic : epics) {
            super.epics.put(epic.getId(), epic);
        }
    }

    private void addSubtask(List<Subtask> subtasks) {
        for (Subtask subtask : subtasks) {
            super.subtasks.put(subtask.getId(), subtask);
        }
    }

    private void addHistory(List<Task> history) {
        for (Task task : history) {
            historyManager.addTask(task);
        }
    }

    @Override
    public void save() {
        List<Task> tasks = getAllTask();
        List<Epic> epics = getAllEpic();
        List<Subtask> subtasks = getAllSubtask();
        List<Task> history = getHistory();

        try {
            kvTaskClient.put("tasks", gson.toJson(tasks));
            kvTaskClient.put("epics", gson.toJson(epics));
            kvTaskClient.put("subtasks", gson.toJson(subtasks));
            kvTaskClient.put("history", gson.toJson(history));
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void loadTasksFromKvServer() throws IOException, InterruptedException {
        String tasksStr = kvTaskClient.load("tasks");
        String epicsStr = kvTaskClient.load("epics");
        String subtasksStr = kvTaskClient.load("subtask");
        String historyStr = kvTaskClient.load("history");

        List<Task> tasks = gson.fromJson(tasksStr, new TypeToken<List<Task>>() {
        }.getType());
        addTask(tasks);
        List<Epic> epics = gson.fromJson(epicsStr, new TypeToken<List<Epic>>() {
        }.getType());
        addEpic(epics);
        List<Subtask> subtasks = gson.fromJson(subtasksStr, new TypeToken<List<Subtask>>() {
        }.getType());
        addSubtask(subtasks);
        List<Task> history = gson.fromJson(historyStr, new TypeToken<List<Task>>() {
        }.getType());
        addHistory(history);
    }
}
