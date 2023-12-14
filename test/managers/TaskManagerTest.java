package managers;

import manager.task.FileBackedTasksManager;
import manager.task.TaskManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import task.Epic;
import task.Subtask;
import task.Task;
import task.TaskStatus;

import java.io.File;
import java.util.List;

public abstract class TaskManagerTest<T extends TaskManager> {

    protected T manager;

    protected Task task1;
    protected Task task2;
    protected Task task3;
    protected Task task4;

    protected Epic epic1;
    protected Epic epic2;
    protected Epic epic3;

    protected Subtask subtask1;
    protected Subtask subtask2;
    protected Subtask subtask3;
    protected Subtask subtask4;
    protected Subtask subtask5;

    @Test
    public void getAllTaskTest() {
        FileBackedTasksManager fileManager;
        manager.createTask(task1);
        manager.createTask(task2);
        manager.createTask(task3);
        manager.createTask(task4);
        Assertions.assertEquals(manager.getAllTask().size(), 4);
        if (manager instanceof FileBackedTasksManager) {
            fileManager = FileBackedTasksManager.loadFromFile(new File("tasks.csv"));
            fileManager.createTaskFromString();
            Assertions.assertEquals(fileManager.getAllTask().size(), 4);
        }
    }

    @Test
    public void getAllEpicTest() {
        FileBackedTasksManager fileManager;
        manager.createEpic(epic1);
        manager.createEpic(epic2);
        manager.createEpic(epic3);
        Assertions.assertEquals(manager.getAllEpic().size(), 3);
        if (manager instanceof FileBackedTasksManager) {
            fileManager = FileBackedTasksManager.loadFromFile(new File("tasks.csv"));
            fileManager.createTaskFromString();
            Assertions.assertEquals(fileManager.getAllEpic().size(), 3);
        }
    }

    @Test
    public void getAllSubtaskTest() {
        FileBackedTasksManager fileManager;
        manager.createEpic(epic1);
        manager.createSubtask(subtask1);
        manager.createSubtask(subtask2);
        manager.createSubtask(subtask3);
        Assertions.assertEquals(manager.getAllSubtask().size(), 3);
        if (manager instanceof FileBackedTasksManager) {
            fileManager = FileBackedTasksManager.loadFromFile(new File("tasks.csv"));
            fileManager.createTaskFromString();
            Assertions.assertEquals(fileManager.getAllSubtask().size(), 3);
        }
    }

    @Test
    public void removeAllTaskTest() {
        FileBackedTasksManager fileManager;
        manager.createTask(task1);
        manager.createTask(task2);
        manager.createTask(task3);
        manager.removeAllTask();
        Assertions.assertTrue(manager.getAllTask().isEmpty());
        if (manager instanceof FileBackedTasksManager) {
            fileManager = FileBackedTasksManager.loadFromFile(new File("tasks.csv"));
            fileManager.createTaskFromString();
            Assertions.assertTrue(fileManager.getAllTask().isEmpty());
        }
    }

    @Test
    public void removeAllEpicTest() {
        FileBackedTasksManager fileManager;
        manager.createEpic(epic1);
        manager.createEpic(epic2);
        manager.createEpic(epic3);
        manager.createSubtask(subtask1);
        manager.createSubtask(subtask2);
        manager.createSubtask(subtask3);
        manager.removeAllEpic();
        Assertions.assertTrue(manager.getAllEpic().isEmpty());
        Assertions.assertTrue(manager.getAllSubtask().isEmpty());
        if (manager instanceof FileBackedTasksManager) {
            fileManager = FileBackedTasksManager.loadFromFile(new File("tasks.csv"));
            fileManager.createTaskFromString();
            Assertions.assertTrue(fileManager.getAllEpic().isEmpty());
            Assertions.assertTrue(fileManager.getAllSubtask().isEmpty());
        }
    }

    @Test
    public void removeAllSubtaskTest() {
        FileBackedTasksManager fileManager;
        manager.createEpic(epic1);
        manager.createSubtask(subtask1);
        manager.createSubtask(subtask2);
        manager.createSubtask(subtask3);
        manager.removeAllSubtask();
        Assertions.assertTrue(manager.getAllSubtask().isEmpty());
        Assertions.assertTrue(manager.getEpic(1).getSubtasks().isEmpty());
        if (manager instanceof FileBackedTasksManager) {
            fileManager = FileBackedTasksManager.loadFromFile(new File("tasks.csv"));
            fileManager.createTaskFromString();
            Assertions.assertTrue(fileManager.getAllSubtask().isEmpty());
        }
    }

    @Test
    public void getTaskTest() {
        FileBackedTasksManager fileManager;
        manager.createTask(task1);
        Task task = manager.getTask(task1.getId());
        Assertions.assertEquals(task.getId(), task1.getId());
        Assertions.assertEquals(manager.getHistory().size(), 1);
        if (manager instanceof FileBackedTasksManager) {
            fileManager = FileBackedTasksManager.loadFromFile(new File("tasks.csv"));
            fileManager.createTaskFromString();
            Assertions.assertEquals(fileManager.getTask(task.getId()).getId(), task.getId());
            Assertions.assertEquals(manager.getHistory().size(), 1);
        }
    }

    @Test
    public void getEpicTest() {
        FileBackedTasksManager fileManager;
        manager.createEpic(epic1);
        Epic epic = manager.getEpic(epic1.getId());
        Assertions.assertEquals(manager.getHistory().size(), 1);
        Assertions.assertEquals(epic.getId(), epic1.getId());
        if (manager instanceof FileBackedTasksManager) {
            fileManager = FileBackedTasksManager.loadFromFile(new File("tasks.csv"));
            fileManager.createTaskFromString();
            Assertions.assertEquals(fileManager.getEpic(epic.getId()).getId(), epic.getId());
            Assertions.assertEquals(manager.getHistory().size(), 1);
        }
    }

    @Test
    public void getSubtaskTest() {
        FileBackedTasksManager fileManager;
        manager.createEpic(epic1);
        manager.createSubtask(subtask1);
        Subtask subtask = manager.getSubtask(subtask1.getId());
        Assertions.assertEquals(manager.getHistory().size(), 1);
        Assertions.assertEquals(subtask.getId(), subtask1.getId());

        RuntimeException ex = Assertions.assertThrows(
                RuntimeException.class,
                () -> manager.createSubtask(subtask4)
        );
        Assertions.assertEquals(ex.getMessage(), "Отсутствует Эпик для подзадачи.");
        if (manager instanceof FileBackedTasksManager) {
            fileManager = FileBackedTasksManager.loadFromFile(new File("tasks.csv"));
            fileManager.createTaskFromString();
            Assertions.assertEquals(fileManager.getSubtask(subtask.getId()).getId(), subtask.getId());
            Assertions.assertEquals(manager.getHistory().size(), 1);
        }
    }

    @Test
    public void updateTaskTest() {
        FileBackedTasksManager fileManager;
        manager.createTask(task1);
        Task task = manager.getTask(task1.getId());
        task.setStatus(TaskStatus.IN_PROGRESS);
        task.setName("update name");
        task.setDescription("update desc");
        manager.updateTask(task);
        task = manager.getTask(task1.getId());
        Assertions.assertEquals(task.getStatus().name(), TaskStatus.IN_PROGRESS.name());
        Assertions.assertEquals(task.getName(), "update name");
        Assertions.assertEquals(task.getDescription(), "update desc");
        if (manager instanceof FileBackedTasksManager) {
            fileManager = FileBackedTasksManager.loadFromFile(new File("tasks.csv"));
            fileManager.createTaskFromString();
            task = fileManager.getTask(task.getId());
            Assertions.assertEquals(task.getName(), "update name");
            Assertions.assertEquals(task.getDescription(), "update desc");
        }
    }

    @Test
    public void updateEpicTest() {
        FileBackedTasksManager fileManager;
        List<Integer> subtaskIds = List.of(2, 3, 4);
        manager.createEpic(epic1);
        manager.createSubtask(subtask1);
        manager.createSubtask(subtask2);
        manager.createSubtask(subtask3);
        Epic epic = manager.getEpic(epic1.getId());
        Subtask sub1 = manager.getSubtask(subtask1.getId());
        Subtask sub2 = manager.getSubtask(subtask2.getId());
        Subtask sub3 = manager.getSubtask(subtask3.getId());
        epic.setName("update name");
        epic.setDescription("update desc");
        manager.updateEpic(epic);
        epic = manager.getEpic(epic1.getId());
        Assertions.assertEquals(epic.getName(), "update name");
        Assertions.assertEquals(epic.getDescription(), "update desc");

        for (int i = 0; i < epic.getSubtasks().size(); i++) {
            Assertions.assertTrue(subtaskIds.contains(epic.getSubtasks().get(i).getId()));
        }
        sub1.setStatus(TaskStatus.IN_PROGRESS);
        sub2.setStatus(TaskStatus.IN_PROGRESS);
        sub3.setStatus(TaskStatus.IN_PROGRESS);
        manager.updateSubtask(sub1);
        manager.updateSubtask(sub2);
        manager.updateSubtask(sub3);
        Assertions.assertEquals(epic.getStatus().name(), TaskStatus.IN_PROGRESS.name());
        sub1.setStatus(TaskStatus.DONE);
        sub2.setStatus(TaskStatus.DONE);
        sub3.setStatus(TaskStatus.DONE);
        manager.updateSubtask(sub1);
        manager.updateSubtask(sub2);
        manager.updateSubtask(sub3);
        Assertions.assertEquals(epic.getStatus().name(), TaskStatus.DONE.name());
        sub1.setStatus(TaskStatus.NEW);
        manager.updateSubtask(sub1);
        Assertions.assertEquals(manager.getHistory().size(), 4);
        Assertions.assertEquals(epic.getStatus().name(), TaskStatus.IN_PROGRESS.name());
        if (manager instanceof FileBackedTasksManager) {
            fileManager = FileBackedTasksManager.loadFromFile(new File("tasks.csv"));
            fileManager.createTaskFromString();
            epic = fileManager.getEpic(epic.getId());
            Assertions.assertEquals(epic.getName(), "update name");
            Assertions.assertEquals(epic.getDescription(), "update desc");
            Assertions.assertEquals(epic.getStatus().name(), TaskStatus.IN_PROGRESS.name());
            Assertions.assertEquals(manager.getHistory().size(), 4);
        }
    }

    @Test
    public void updateSubtaskTest() {
        FileBackedTasksManager fileManager;
        manager.createEpic(epic1);
        manager.createSubtask(subtask1);
        Subtask sub1 = manager.getSubtask(subtask1.getId());
        sub1.setName("update name");
        sub1.setDescription("update desc");
        manager.updateSubtask(sub1);
        sub1 = manager.getSubtask(subtask1.getId());
        Assertions.assertEquals(sub1.getName(), "update name");
        Assertions.assertEquals(sub1.getDescription(), "update desc");
        if (manager instanceof FileBackedTasksManager) {
            fileManager = FileBackedTasksManager.loadFromFile(new File("tasks.csv"));
            fileManager.createTaskFromString();
            sub1 = fileManager.getSubtask(sub1.getId());
            Assertions.assertEquals(sub1.getName(), "update name");
            Assertions.assertEquals(sub1.getDescription(), "update desc");
        }
    }

    @Test
    public void removeTaskByIdTest() {
        FileBackedTasksManager fileManager;
        manager.createTask(task1);
        Task task = manager.getTask(1);
        manager.removeTaskById(task.getId());
        Assertions.assertNull(manager.getTask(task1.getId()));
        if (manager instanceof FileBackedTasksManager) {
            fileManager = FileBackedTasksManager.loadFromFile(new File("tasks.csv"));
            fileManager.createTaskFromString();
            Assertions.assertNull(fileManager.getTask(task.getId()));
        }
    }

    @Test
    public void removeEpicByIdTest() {
        FileBackedTasksManager fileManager;
        manager.createEpic(epic1);
        manager.createSubtask(subtask1);
        manager.createSubtask(subtask2);
        Epic epic = manager.getEpic(1);
        manager.removeEpicById(epic1.getId());
        Assertions.assertNull(manager.getEpic(epic1.getId()));
        Assertions.assertTrue(manager.getAllSubtask().isEmpty());
        if (manager instanceof FileBackedTasksManager) {
            fileManager = FileBackedTasksManager.loadFromFile(new File("tasks.csv"));
            fileManager.createTaskFromString();
            Assertions.assertNull(fileManager.getEpic(epic.getId()));
            Assertions.assertTrue(fileManager.getAllSubtask().isEmpty());
        }
    }


    @Test
    public void removeSubtaskByIdTest() {
        FileBackedTasksManager fileManager;
        manager.createEpic(epic1);
        manager.createSubtask(subtask1);
        Epic epic = manager.getEpic(epic1.getId());
        Subtask sub1 = manager.getSubtask(subtask1.getId());
        manager.removeSubtaskById(subtask1.getId());
        Assertions.assertTrue(epic.getSubtasks().isEmpty());
        Assertions.assertNull(manager.getSubtask(subtask1.getId()));
        if (manager instanceof FileBackedTasksManager) {
            fileManager = FileBackedTasksManager.loadFromFile(new File("tasks.csv"));
            fileManager.createTaskFromString();
            Assertions.assertNull(fileManager.getSubtask(sub1.getId()));
        }
    }

    @Test
    public void getSubtaskByEpicTest() {
        FileBackedTasksManager fileManager;
        List<Integer> subtaskIds = List.of(2, 3);
        manager.createEpic(epic1);
        manager.createSubtask(subtask1);
        manager.createSubtask(subtask2);
        Epic epic = manager.getEpic(epic1.getId());
        for (int i = 0; i < epic.getSubtasks().size(); i++) {
            Assertions.assertTrue(subtaskIds.contains(epic.getSubtasks().get(i).getId()));
        }
        if (manager instanceof FileBackedTasksManager) {
            fileManager = FileBackedTasksManager.loadFromFile(new File("tasks.csv"));
            fileManager.createTaskFromString();
            for (int i = 0; i < epic.getSubtasks().size(); i++) {
                Assertions.assertTrue(subtaskIds.contains(epic.getSubtasks().get(i).getId()));
            }
        }
    }
}
