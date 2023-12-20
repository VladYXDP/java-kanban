package manager;

import manager.history.HistoryManager;
import manager.history.InMemoryHistoryManager;
import manager.task.FileBackedTasksManager;
import manager.task.InMemoryTaskManager;
import manager.task.TaskManager;

import java.io.File;

public class Managers {

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }

    public static TaskManager getDefault() {
        return new InMemoryTaskManager();
    }

    public static TaskManager getFileManager() {
        FileBackedTasksManager fileManager = FileBackedTasksManager.loadFromFile(new File("httpTaskFile.csv"));
        fileManager.createTaskFromString();
        return fileManager;
    }
}
