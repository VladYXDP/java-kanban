package managers;

import manager.task.FileBackedTasksManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import java.io.File;

public class InFileBackedTaskManagerTest extends TaskManagerTest<FileBackedTasksManager> {

    @BeforeEach
    public void init() {
        manager = new FileBackedTasksManager(new File("tasks.csv"), null);
        manager.removeAllTask();
        manager.removeAllEpic();
    }

    @AfterEach
    public void loadTaskFromFileTest() {
        FileBackedTasksManager fileManager = FileBackedTasksManager.loadFromFile(new File("tasks.csv"));
        fileManager.createTaskFromString();
    }
}
