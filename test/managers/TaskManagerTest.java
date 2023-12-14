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
        if (manager instanceof FileBackedTasksManager) {
            fileManager = FileBackedTasksManager.loadFromFile(new File("tasks.csv"));
            fileManager.createTaskFromString();
            Assertions.assertEquals(fileManager.getTask(task.getId()).getId(), task.getId());
        }
    }

    @Test
    public void getEpicTest() {
        FileBackedTasksManager fileManager;
        manager.createEpic(epic1);
        Epic epic = manager.getEpic(epic1.getId());
        Assertions.assertEquals(epic.getId(), epic1.getId());
        if (manager instanceof FileBackedTasksManager) {
            fileManager = FileBackedTasksManager.loadFromFile(new File("tasks.csv"));
            fileManager.createTaskFromString();
            Assertions.assertEquals(fileManager.getEpic(epic.getId()).getId(), epic.getId());
        }
    }

    @Test
    public void getSubtaskTest() {
        FileBackedTasksManager fileManager;
        manager.createEpic(epic1);
        manager.createSubtask(subtask1);
        Subtask subtask = manager.getSubtask(subtask1.getId());
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
        Assertions.assertEquals(epic.getStatus().name(), TaskStatus.IN_PROGRESS.name());
        if (manager instanceof FileBackedTasksManager) {
            fileManager = FileBackedTasksManager.loadFromFile(new File("tasks.csv"));
            fileManager.createTaskFromString();
            epic = fileManager.getEpic(epic.getId());
            Assertions.assertEquals(epic.getName(), "update name");
            Assertions.assertEquals(epic.getDescription(), "update desc");
            Assertions.assertEquals(epic.getStatus().name(), TaskStatus.IN_PROGRESS.name());
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

//    @Test
//    public void checkEpicAndSubtask() {
//        manager.createEpic(epic1);
//        Epic epic = manager.getEpic(epic1.getId());
//        Assertions.assertEquals(epic.getId(), 1, "Разные индексы");
//        Assertions.assertEquals(manager.getAllEpic().size(), 1, "Неверно заполняется список Эпиков");
//
//        manager.createSubtask(subtask1);
//        manager.createSubtask(subtask2);
//        Subtask sub1 = manager.getSubtask(subtask1.getId());
//        Subtask sub2 = manager.getSubtask(subtask2.getId());
//        Assertions.assertEquals(subtask1.getId(), sub1.getId());
//        Assertions.assertEquals(subtask1.getEpicId(), epic1.getId());
//        Assertions.assertEquals(subtask2.getId(), sub2.getId());
//        Assertions.assertEquals(subtask2.getEpicId(), epic1.getId());
//        Assertions.assertEquals(epic.getStatus().name(), TaskStatus.NEW.name(), "Неверный статус Эпика");
//        Assertions.assertEquals(manager.getEpic(epic1.getId()).getSubtasks().size(), 2, "Неверно заполняется список подзадач Эпика");
//        Assertions.assertEquals(manager.getAllSubtask().size(), 2, "Неверно заполняется список подзадач");
//
//        sub1.setStatus(TaskStatus.DONE);
//        sub2.setStatus(TaskStatus.DONE);
//        manager.updateSubtask(manager.getSubtask(sub1.getId()));
//        manager.updateSubtask(manager.getSubtask(sub2.getId()));
//
//        Assertions.assertEquals(epic.getStatus().name(), TaskStatus.DONE.name(), "Неверный статус Эпика");
//
//        subtask1.setStatus(TaskStatus.NEW);
//        updateSubtask(subtask1);
//        Assertions.assertEquals(epic.getStatus().name(), TaskStatus.IN_PROGRESS.name(), "Неверный статус Эпика");
//
//        subtask1.setStatus(TaskStatus.IN_PROGRESS);
//        subtask2.setStatus(TaskStatus.IN_PROGRESS);
//        updateTask(subtask1);
//        updateTask(subtask2);
//        updateSubtask(subtask1);
//        updateSubtask(subtask2);
//        Assertions.assertEquals(epic.getStatus().name(), TaskStatus.IN_PROGRESS.name(), "Неверный статус Эпика");
//        Assertions.assertEquals(getSubtask(subtaskId1).getStatus().name(), TaskStatus.IN_PROGRESS.name(), "Не обновляется подзадача");
//
//        epic.setName("New name");
//        updateEpic(epic);
//        Assertions.assertEquals(getEpic(epicId).getName(), "New name", "Не обновляется Эпик");
//
//
//        removeAllSubtask();
//        Assertions.assertTrue(getAllSubtask().isEmpty());
//        Assertions.assertEquals(epic.getStatus().name(), TaskStatus.NEW.name(), "Неверный статус Эпика");
//        Assertions.assertTrue(epic.getSubtasks().isEmpty(), "Список подзадач Эпика не пустой");
//    }
//
//    @Test
//    public void checkTask() {
//        setManager(new FileBackedTasksManager(new File("1.csv"), null));
//        int globalTaskId = addNewTask(new Task("Task name", "Task desc", LocalDateTime.now(),
//                Duration.ofHours(8)));
//        Task task = getTask(globalTaskId);
//        Assertions.assertEquals(task.getStatus().name(), TaskStatus.NEW.name(), "Неверный статус задачи");
//        Assertions.assertEquals(task.getId(), globalTaskId, "Ошибка создания задачи");
//        Assertions.assertEquals(getAllTask().size(), 1, "Список задач не равен 1");
//
//        task.setStatus(TaskStatus.DONE);
//        updateTask(task);
//        Task updateTask = getTask(globalTaskId);
//        Assertions.assertEquals(updateTask.getStatus().name(), TaskStatus.DONE.name(), "Задача не обновляется");
//        removeAllTask();
//        Assertions.assertTrue(getAllTask().isEmpty(), "Не пустой список задач после удаления");
//    }
//
//    @Test
//    public void checkRemovingById() {
//        setManager(new FileBackedTasksManager(new File("1.csv"), null));
//        setManager(new FileBackedTasksManager(new File("checkRemovingById.csv"), null));
//        int taskId = addNewTask(new Task("TaskName", "TaskDesc", LocalDateTime.now(), Duration.ofHours(5)));
//        addNewTask(new Task("TaskName", "TaskDesc", LocalDateTime.parse("2023-12-30T18:44:06.456050"),
//                Duration.ofHours(1)));
//        int epicId = addNewEpic(new Epic("Epic Name", "Epic desc",
//                LocalDateTime.parse("2024-01-02T18:44:06.456050")));
//        int subtaskId1 = addNewSubtask(new Subtask("Subtask name", "Subtask desc", epicId,
//                LocalDateTime.parse("2024-01-02T18:44:06.456050"), Duration.ofHours(2)));
//        int subtaskId2 = addNewSubtask(new Subtask("Subtask name", "Subtask desc", epicId,
//                LocalDateTime.parse("2024-01-03T18:44:06.456050"), Duration.ofHours(2)));
//
//        removeTaskById(taskId);
//        removeSubtaskById(subtaskId2);
//        Assertions.assertNull(getTask(taskId), "Ошибка удаления задачи");
//        Assertions.assertNull(getSubtask(subtaskId2), "Ошибка удаления подзадачи");
//        removeEpicById(epicId);
//        Assertions.assertNull(getEpic(epicId), "Ошибка удаления эпика");
//        Assertions.assertNull(getSubtask(subtaskId1), "Ошибка удаления подзадачи после удаления эпика");
//    }
//
//    @Test
//    public void checkSaveAndLoadFileWithoutTasks() {
//        File file = new File("checkSaveAndLoadWithoutTasks.csv");
//        setManager(new FileBackedTasksManager(file, null));
//        addNewTask(null);
//        addNewEpic(null);
//        addNewSubtask(null);
//        getTask(3);
//        getEpic(4);
//        FileBackedTasksManager fileManager = FileBackedTasksManager.loadFromFile(file);
//        fileManager.createTaskFromString();
//        Assertions.assertTrue(fileManager.getAllTask().isEmpty(), "Список задач должен быть пустым");
//        Assertions.assertTrue(fileManager.getAllEpic().isEmpty(), "Список эпиков должен быть пустым");
//        Assertions.assertTrue(fileManager.getAllSubtask().isEmpty(), "Список подзадач должен быть пустым");
//        Assertions.assertTrue(fileManager.getHistory().isEmpty(), "Список истории должен быть пустым");
//    }
//
//    @Test
//    public void checkSaveAndLoadFileWithOnlyEpics() {
//        File file = new File("checkSaveAndLoadWithOnlyEpics.csv");
//        setManager(new FileBackedTasksManager(file, null));
//        int epic1 = addNewEpic(new Epic("Epicname1", "Epicdesc1", LocalDateTime.parse("2024-01-02T18:44:06.456050")));
//        int epic2 = addNewEpic(new Epic("Epicname2", "Epicdesc2", LocalDateTime.parse("2024-01-03T18:44:06.456050")));
//        int epic3 = addNewEpic(new Epic("Epicname3", "Epicdesc3", LocalDateTime.parse("2024-01-04T18:44:06.456050")));
//        getEpic(epic1);
//        getEpic(epic2);
//        getEpic(epic3);
//        FileBackedTasksManager fileManager = FileBackedTasksManager.loadFromFile(file);
//        fileManager.createTaskFromString();
//        Assertions.assertNotNull(fileManager.getEpic(epic1), "Эпик не найден");
//        Assertions.assertNotNull(fileManager.getEpic(epic2),"Эпик не найден");
//        Assertions.assertNotNull(fileManager.getEpic(epic3), "Эпик не найден");
//        Assertions.assertEquals(fileManager.getHistory().size(), 3);
//    }
}
