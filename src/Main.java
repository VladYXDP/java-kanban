import manager.Managers;
import manager.task.FileBackedTasksManager;
import manager.task.HttpTaskManager;
import manager.task.TaskManager;
import server.HttpTaskServer;
import server.kv.KVServer;
import task.Epic;
import task.Subtask;
import task.Task;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;

public class Main {

    public static void main(String[] args) throws IOException, InterruptedException {
        new KVServer().start();
        new HttpTaskServer().start();

//        TaskManager manager = Managers.getDefault();
//        Task task = new Task("Task name1", "Desc Name1", LocalDateTime.parse("2020-01-20T10:01:01")
//                , Duration.ofSeconds(10000));
//        Epic epic = new Epic("Epic name", "Desc", LocalDateTime.parse("2024-12-11T10:10:11"));
//        Subtask subtask = new Subtask("Subtask name", "Desc", 2, LocalDateTime.parse("2024-12-11T11:11:11"),
//                Duration.ofSeconds(10000));
//        manager.createTask(task);
//        manager.createEpic(epic);
//        manager.createSubtask(subtask);
    }
}
