import manager.Managers;
import manager.task.FileBackedTasksManager;
import manager.task.TaskManager;
import task.Epic;
import task.Subtask;
import task.Task;

import java.io.File;
import java.time.Duration;
import java.time.LocalDateTime;

public class Main {

    public static void main(String[] args) {
        TaskManager taskManager = Managers.getDefault();
        Task task = new Task("name", "desc", LocalDateTime.now(), Duration.ofHours(12));
        taskManager.createTask(task);
        Task task1 = new Task("name1", "desc", LocalDateTime.now(), Duration.ofHours(12));
        taskManager.createTask(task1);

        TaskManager fileManager = new FileBackedTasksManager(new File("1.csv"), null);
        fileManager.createTask(new Task("Epic Name", "Epic desc",
                LocalDateTime.parse("2024-01-02T18:44:06.456050"), Duration.ofHours(1)));
        fileManager.createEpic(new Epic("Epic Name", "Epic desc",
                LocalDateTime.parse("2024-01-03T18:44:06.456050")));
        fileManager.createSubtask(new Subtask("Epic Name", "Epic desc", 2,
                LocalDateTime.parse("2024-01-03T18:44:06.456050"), Duration.ofHours(1)));

        fileManager.getTask(1);
    }
}
