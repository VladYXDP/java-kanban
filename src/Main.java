import manager.Managers;
import manager.task.FileBackedTasksManager;
import manager.task.TaskManager;
import task.Epic;
import task.Subtask;
import task.Task;
import task.TaskStatus;

import java.io.File;
import java.time.Duration;
import java.time.LocalDateTime;

public class Main {

    public static void main(String[] args) {
        TaskManager taskManager = Managers.getDefault();
        TaskManager fileManager = new FileBackedTasksManager(new File("task.csv"), null);
        Task task = new Task("name", "desc", Duration.ofHours(12));
        taskManager.createTask(task);
        Task task1 = new Task("name1", "desc", Duration.ofHours(12));
        taskManager.createTask(task1);
        task1.setStartTime(LocalDateTime.parse("2023-12-09T18:44:06.456050"));
        taskManager.getPrioritizedTasks().forEach(System.out::println);

//        fileManager.createEpic(new Epic("name", "desc"));
//        fileManager.createSubtask(new Subtask("name", "desc", 3, Duration.ofHours(12)));
//        fileManager.createSubtask(new Subtask("name", "desc", 3, Duration.ofHours(10)));
    }
}
