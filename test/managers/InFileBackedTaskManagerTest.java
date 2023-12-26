package managers;

import manager.task.FileBackedTasksManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

public class InFileBackedTaskManagerTest extends TaskManagerTest<FileBackedTasksManager> {

    @BeforeEach
    public void init() {
        manager = new FileBackedTasksManager("tasks.csv", null);
        manager.removeAllTask();
        manager.removeAllEpic();
    }

    @AfterEach
    public void loadTaskFromFileTest() {
        FileBackedTasksManager fileManager = FileBackedTasksManager.load("tasks.csv");
        fileManager.createTaskFromString();
    }
}
