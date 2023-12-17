package managers;

import manager.task.FileBackedTasksManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import task.Epic;
import task.Subtask;
import task.Task;

import java.io.File;
import java.time.Duration;
import java.time.LocalDateTime;

public class InFileBackedTaskManagerTest extends TaskManagerTest<FileBackedTasksManager> {

    @BeforeEach
    public void init() {
        manager = new FileBackedTasksManager(new File("tasks.csv"), null);
        manager.removeAllTask();
        manager.removeAllEpic();
        task1 = new Task("Task name1", "Task desc1"
                , LocalDateTime.parse("2023-12-30T18:44:06.456050"), Duration.ofHours(8));
        task2 = new Task("Task name2", "Task desc2"
                , LocalDateTime.parse("2023-12-31T18:44:06.456050"), Duration.ofHours(8));
        task3 = new Task("Task name3", "Task desc3"
                , LocalDateTime.parse("2024-01-01T18:44:06.456050"), Duration.ofHours(8));
        task4 = new Task("Task name4", "Task desc4"
                , LocalDateTime.parse("2024-01-02T18:44:06.456050"), Duration.ofHours(8));

        epic1 = new Epic("Epic Name1", "Epic Desc1", LocalDateTime.parse("2024-01-04T18:44:06.456050"));
        epic2 = new Epic("Epic Name2", "Epic Desc2", LocalDateTime.parse("2024-01-05T18:44:06.456050"));
        epic3 = new Epic("Epic Name3", "Epic Desc3", LocalDateTime.parse("2024-01-06T18:44:06.456050"));

        subtask1 = new Subtask("Subtask Name1", "Subtask Desc1", 1, LocalDateTime.parse("2024-01-07T18:44:06.456050"),
                Duration.ofHours(10));
        subtask2 = new Subtask("Subtask Name2", "Subtask Desc2", 1, LocalDateTime.parse("2024-01-08T18:44:06.456050"),
                Duration.ofHours(10));
        subtask3 = new Subtask("Subtask Name3", "Subtask Desc3", 1, LocalDateTime.parse("2024-01-09T18:44:06.456050"),
                Duration.ofHours(10));
        subtask4 = new Subtask("Subtask Name4", "Subtask Desc4", 2, LocalDateTime.parse("2024-01-10T18:44:06.456050"),
                Duration.ofHours(10));
        subtask5 = new Subtask("Subtask Name5", "Subtask Desc5", 3, LocalDateTime.parse("2024-01-11T18:44:06.456050"),
                Duration.ofHours(10));
    }

    @AfterEach
    public void loadTaskFromFileTest() {
        FileBackedTasksManager fileManager = FileBackedTasksManager.loadFromFile(new File("tasks.csv"));
        fileManager.createTaskFromString();
    }
}
