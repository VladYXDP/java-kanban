package manager;

import manager.history.HistoryManager;
import manager.history.InMemoryHistoryManager;
import manager.task.FileBackedTasksManager;
import manager.task.HttpTaskManager;
import manager.task.TaskManager;

import java.io.IOException;

public class Managers {

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }

    public static TaskManager getDefault() throws IOException, InterruptedException {
        return new HttpTaskManager("http://localhost:8078/");
    }

    public static TaskManager getFileManager() {
        FileBackedTasksManager fileManager = FileBackedTasksManager.loadFromFile("httpTaskFile.csv");
        fileManager.createTaskFromString();
        return fileManager;
    }
}
