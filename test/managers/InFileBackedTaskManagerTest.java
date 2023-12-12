package managers;

import manager.task.FileBackedTasksManager;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import task.Epic;
import task.Subtask;
import task.Task;
import task.TaskStatus;

import java.io.File;
import java.time.Duration;
import java.time.LocalDateTime;

public class InFileBackedTaskManagerTest extends TaskManagerTest<FileBackedTasksManager> {

    @Test
    public void checkEpicAndSubtask() {
        setManager(new FileBackedTasksManager(new File("checkEpicAndSubtask.csv"), null));
        int epicId = addNewEpic(new Epic("Epic Name", "Epic Desc", LocalDateTime.now()));
        Epic epic = getEpic(epicId);
        Assertions.assertEquals(epic.getId(), epicId, "Разные индексы");
        Assertions.assertEquals(getAllEpic().size(), 1, "Неверно заполняется список Эпиков");

        int subtaskId1 = addNewSubtask(new Subtask("Subtask Name", "Subtask Desc", epicId, LocalDateTime.now(),
                Duration.ofHours(10)));
        int subtaskId2 = addNewSubtask(new Subtask("Subtask Name", "Subtask Desc", epicId,
                LocalDateTime.parse("2023-12-20T18:44:06.456050"), Duration.ofHours(12)));
        Subtask subtask1 = getSubtask(subtaskId1);
        Subtask subtask2 = getSubtask(subtaskId2);
        Assertions.assertEquals(subtask1.getId(), subtaskId1);
        Assertions.assertEquals(subtask1.getEpicId(), epicId);
        Assertions.assertEquals(subtask2.getId(), subtaskId2);
        Assertions.assertEquals(subtask2.getEpicId(), epicId);
        Assertions.assertEquals(epic.getStatus().name(), TaskStatus.NEW.name(), "Неверный статус Эпика");
        Assertions.assertEquals(getEpic(epicId).getSubtasks().size(), 2, "Неверно заполняется список подзадач Эпика");
        Assertions.assertEquals(getAllSubtask().size(), 2, "Неверно заполняется список подзадач");

        subtask1.setStatus(TaskStatus.DONE);
        subtask2.setStatus(TaskStatus.DONE);
        updateSubtask(getSubtask(subtaskId1));
        updateSubtask(getSubtask(subtaskId2));

        Assertions.assertEquals(epic.getStatus().name(), TaskStatus.DONE.name(), "Неверный статус Эпика");

        subtask1.setStatus(TaskStatus.NEW);
        updateSubtask(subtask1);
        Assertions.assertEquals(epic.getStatus().name(), TaskStatus.IN_PROGRESS.name(), "Неверный статус Эпика");

        subtask1.setStatus(TaskStatus.IN_PROGRESS);
        subtask2.setStatus(TaskStatus.IN_PROGRESS);
        updateSubtask(subtask1);
        updateSubtask(subtask2);
        Assertions.assertEquals(epic.getStatus().name(), TaskStatus.IN_PROGRESS.name(), "Неверный статус Эпика");
        Assertions.assertEquals(getSubtask(subtaskId1).getStatus().name(), TaskStatus.IN_PROGRESS.name(), "Не обновляется подзадача");

        epic.setName("New name");
        updateEpic(epic);
        Assertions.assertEquals(getEpic(epicId).getName(), "New name", "Не обновляется Эпик");


        removeAllSubtask();
        Assertions.assertTrue(getAllSubtask().isEmpty());
        Assertions.assertEquals(epic.getStatus().name(), TaskStatus.NEW.name(), "Неверный статус Эпика");
        Assertions.assertTrue(epic.getSubtasks().isEmpty(), "Список подзадач Эпика не пустой");
    }

    @Test
    public void checkTask() {
        setManager(new FileBackedTasksManager(new File("1.csv"), null));
        int globalTaskId = addNewTask(new Task("Task name", "Task desc", LocalDateTime.now(),
                Duration.ofHours(8)));
        Task task = getTask(globalTaskId);
        Assertions.assertEquals(task.getStatus().name(), TaskStatus.NEW.name(), "Неверный статус задачи");
        Assertions.assertEquals(task.getId(), globalTaskId, "Ошибка создания задачи");
        Assertions.assertEquals(getAllTask().size(), 1, "Список задач не равен 1");

        task.setStatus(TaskStatus.DONE);
        updateTask(task);
        Task updateTask = getTask(globalTaskId);
        Assertions.assertEquals(updateTask.getStatus().name(), TaskStatus.DONE.name(), "Задача не обновляется");
        removeAllTask();
        Assertions.assertTrue(getAllTask().isEmpty(), "Не пустой список задач после удаления");
    }

    @Test
    public void checkRemovingById() {
        setManager(new FileBackedTasksManager(new File("checkRemovingById.csv"), null));
        int taskId = addNewTask(new Task("TaskName", "TaskDesc", LocalDateTime.now(), Duration.ofHours(5)));
        addNewTask(new Task("TaskName", "TaskDesc", LocalDateTime.parse("2023-12-30T18:44:06.456050"),
                Duration.ofHours(1)));
        int epicId = addNewEpic(new Epic("Epic Name", "Epic desc",
                LocalDateTime.parse("2024-01-02T18:44:06.456050")));
        int subtaskId1 = addNewSubtask(new Subtask("Subtask name", "Subtask desc", epicId,
                LocalDateTime.parse("2024-01-02T18:44:06.456050"), Duration.ofHours(2)));
        int subtaskId2 = addNewSubtask(new Subtask("Subtask name", "Subtask desc", epicId,
                LocalDateTime.parse("2024-01-03T18:44:06.456050"), Duration.ofHours(2)));

        removeTaskById(taskId);
        removeSubtaskById(subtaskId2);
        Assertions.assertNull(getTask(taskId), "Ошибка удаления задачи");
        Assertions.assertNull(getSubtask(subtaskId2), "Ошибка удаления подзадачи");
        removeEpicById(epicId);
        Assertions.assertNull(getEpic(epicId), "Ошибка удаления эпика");
        Assertions.assertNull(getSubtask(subtaskId1), "Ошибка удаления подзадачи после удаления эпика");
    }

    @Test
    public void checkSaveAndLoadFileWithoutTasks() {
        File file = new File("checkSaveAndLoadWithoutTasks.csv");
        setManager(new FileBackedTasksManager(file, null));
        addNewTask(null);
        addNewEpic(null);
        addNewSubtask(null);
        getTask(3);
        getEpic(4);
        FileBackedTasksManager fileManager = FileBackedTasksManager.loadFromFile(file);
        fileManager.createTaskFromString();
        Assertions.assertTrue(fileManager.getAllTask().isEmpty(), "Список задач должен быть пустым");
        Assertions.assertTrue(fileManager.getAllEpic().isEmpty(), "Список эпиков должен быть пустым");
        Assertions.assertTrue(fileManager.getAllSubtask().isEmpty(), "Список подзадач должен быть пустым");
        Assertions.assertTrue(fileManager.getHistory().isEmpty(), "Список истории должен быть пустым");
    }

    @Test
    public void checkSaveAndLoadFileWithOnlyEpics() {
        File file = new File("checkSaveAndLoadWithOnlyEpics.csv");
        setManager(new FileBackedTasksManager(file, null));
        int epic1 = addNewEpic(new Epic("Epicname1", "Epicdesc1", LocalDateTime.parse("2024-01-02T18:44:06.456050")));
        int epic2 = addNewEpic(new Epic("Epicname2", "Epicdesc2", LocalDateTime.parse("2024-01-03T18:44:06.456050")));
        int epic3 = addNewEpic(new Epic("Epicname3", "Epicdesc3", LocalDateTime.parse("2024-01-04T18:44:06.456050")));
        getEpic(epic1);
        getEpic(epic2);
        getEpic(epic3);
        FileBackedTasksManager fileManager = FileBackedTasksManager.loadFromFile(file);
        fileManager.createTaskFromString();
        Assertions.assertNotNull(fileManager.getEpic(epic1), "Эпик не найден");
        Assertions.assertNotNull(fileManager.getEpic(epic2),"Эпик не найден");
        Assertions.assertNotNull(fileManager.getEpic(epic3), "Эпик не найден");
        Assertions.assertEquals(fileManager.getHistory().size(), 3);
    }
}
