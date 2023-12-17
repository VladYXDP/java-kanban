package managers;

import manager.task.TaskManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import task.Epic;
import task.Subtask;
import task.Task;
import task.TaskStatus;

import java.time.Duration;
import java.time.LocalDateTime;
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

    @BeforeEach
    public void create() {
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

    @Test
    public void getAllTaskTest() {
        manager.createTask(task1);
        manager.createTask(task2);
        manager.createTask(task3);
        manager.createTask(task4);

        Assertions.assertEquals(manager.getAllTask().size(), 4);
    }

    @Test
    public void getAllEpicTest() {
        manager.createEpic(epic1);
        manager.createEpic(epic2);
        manager.createEpic(epic3);

        Assertions.assertEquals(manager.getAllEpic().size(), 3);
    }

    @Test
    public void getAllSubtaskTest() {
        manager.createEpic(epic1);
        manager.createSubtask(subtask1);
        manager.createSubtask(subtask2);
        manager.createSubtask(subtask3);

        Assertions.assertEquals(manager.getAllSubtask().size(), 3);
    }

    @Test
    public void removeAllTaskTest() {
        manager.createTask(task1);
        manager.createTask(task2);
        manager.createTask(task3);

        manager.removeAllTask();

        Assertions.assertTrue(manager.getAllTask().isEmpty());
    }

    @Test
    public void removeAllEpicTest() {
        manager.createEpic(epic1);
        manager.createEpic(epic2);
        manager.createEpic(epic3);
        manager.createSubtask(subtask1);
        manager.createSubtask(subtask2);
        manager.createSubtask(subtask3);

        manager.removeAllEpic();

        Assertions.assertTrue(manager.getAllEpic().isEmpty());
        Assertions.assertTrue(manager.getAllSubtask().isEmpty());
    }

    @Test
    public void removeAllSubtaskTest() {
        manager.createEpic(epic1);
        manager.createSubtask(subtask1);
        manager.createSubtask(subtask2);
        manager.createSubtask(subtask3);

        manager.removeAllSubtask();

        Assertions.assertTrue(manager.getAllSubtask().isEmpty());
        Assertions.assertTrue(manager.getEpic(1).getSubtasks().isEmpty());
    }

    @Test
    public void getTaskTest() {
        manager.createTask(task1);

        Task task = manager.getTask(task1.getId());

        Assertions.assertEquals(task.getId(), task1.getId());
    }

    @Test
    public void getEpicTest() {
        manager.createEpic(epic1);

        Epic epic = manager.getEpic(epic1.getId());

        Assertions.assertEquals(epic, epic1);
    }

    @Test
    public void getSubtaskTest() {
        manager.createEpic(epic1);
        manager.createSubtask(subtask1);

        Subtask subtask = manager.getSubtask(subtask1.getId());

        Assertions.assertEquals(subtask, subtask1);
    }

    @Test
    public void createSubtaskWithoutEpic() {
        RuntimeException ex = Assertions.assertThrows(
                RuntimeException.class,
                () -> manager.createSubtask(subtask4)
        );
        Assertions.assertEquals(ex.getMessage(), "Отсутствует Эпик для подзадачи.");
    }

    @Test
    public void updateTaskTest() {
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
    }

    @Test
    public void updateEpicForNameAndDescTest() {
        manager.createEpic(epic1);
        Epic epic = manager.getEpic(epic1.getId());
        epic.setName("update name");
        epic.setDescription("update desc");

        manager.updateEpic(epic);

        epic = manager.getEpic(epic1.getId());
        Assertions.assertEquals(epic.getName(), "update name");
        Assertions.assertEquals(epic.getDescription(), "update desc");
    }

    @Test
    public void epicForStartTimeTest() {
        manager.createEpic(epic1);
        manager.createSubtask(subtask1);
        manager.createSubtask(subtask2);

        Assertions.assertEquals(epic1.getStartTime(), subtask1.getStartTime());
    }

    @Test
    public void epicForEndTimeTest() {
        manager.createEpic(epic1);
        manager.createSubtask(subtask1);
        manager.createSubtask(subtask2);

        Assertions.assertEquals(epic1.getEndTime(), subtask2.getEndTime());
    }

    @Test
    public void epicForDurationTest() {
        manager.createEpic(epic1);
        manager.createSubtask(subtask1);
        manager.createSubtask(subtask2);

        Assertions.assertEquals(epic1.getDuration(), subtask2.getDuration().plus(subtask1.getDuration()));
    }

    @Test
    public void subtaskListForEpicTest() {
        List<Integer> subtaskIds = List.of(2, 3, 4);
        manager.createEpic(epic1);
        manager.createSubtask(subtask1);
        manager.createSubtask(subtask2);
        manager.createSubtask(subtask3);

        Epic epic = manager.getEpic(epic1.getId());
        for (int i = 0; i < epic.getSubtasks().size(); i++) {
            Assertions.assertTrue(subtaskIds.contains(epic.getSubtasks().get(i).getId()));
        }
    }

    @Test
    public void epicForAllInProgressStatusTest() {
        manager.createEpic(epic1);
        manager.createSubtask(subtask1);
        manager.createSubtask(subtask2);
        manager.createSubtask(subtask3);
        subtask1.setStatus(TaskStatus.IN_PROGRESS);
        subtask2.setStatus(TaskStatus.IN_PROGRESS);
        subtask3.setStatus(TaskStatus.IN_PROGRESS);
        manager.updateSubtask(subtask1);
        manager.updateSubtask(subtask2);
        manager.updateSubtask(subtask3);

        Epic epic = manager.getEpic(epic1.getId());
        Assertions.assertEquals(epic.getStatus().name(), TaskStatus.IN_PROGRESS.name());
    }

    @Test
    public void epicForAllDoneStatusTest() {
        manager.createEpic(epic1);
        manager.createSubtask(subtask1);
        manager.createSubtask(subtask2);
        manager.createSubtask(subtask3);
        subtask1.setStatus(TaskStatus.DONE);
        subtask2.setStatus(TaskStatus.DONE);
        subtask3.setStatus(TaskStatus.DONE);
        manager.updateSubtask(subtask1);
        manager.updateSubtask(subtask2);
        manager.updateSubtask(subtask3);

        Epic epic = manager.getEpic(epic1.getId());
        Assertions.assertEquals(epic.getStatus().name(), TaskStatus.DONE.name());
    }

    @Test
    public void epicForAllDoneWithOneNewStatusTest() {
        manager.createEpic(epic1);
        manager.createSubtask(subtask1);
        manager.createSubtask(subtask2);
        manager.createSubtask(subtask3);
        subtask1.setStatus(TaskStatus.DONE);
        subtask2.setStatus(TaskStatus.DONE);
        subtask3.setStatus(TaskStatus.NEW);
        manager.updateSubtask(subtask1);
        manager.updateSubtask(subtask2);
        manager.updateSubtask(subtask3);

        Epic epic = manager.getEpic(epic1.getId());
        Assertions.assertEquals(epic.getStatus().name(), TaskStatus.IN_PROGRESS.name());
    }

    @Test
    public void updateSubtaskTest() {
        manager.createEpic(epic1);
        manager.createSubtask(subtask1);
        Subtask sub1 = manager.getSubtask(subtask1.getId());
        sub1.setName("update name");
        sub1.setDescription("update desc");

        manager.updateSubtask(sub1);

        sub1 = manager.getSubtask(subtask1.getId());
        Assertions.assertEquals(sub1.getName(), "update name");
        Assertions.assertEquals(sub1.getDescription(), "update desc");
    }

    @Test
    public void removeTaskByIdTest() {
        manager.createTask(task1);
        Task task = manager.getTask(1);

        manager.removeTaskById(task.getId());

        Assertions.assertNull(manager.getTask(task1.getId()));
    }

    @Test
    public void removeEpicByIdTest() {
        manager.createEpic(epic1);
        manager.createSubtask(subtask1);
        manager.createSubtask(subtask2);

        manager.removeEpicById(epic1.getId());

        Assertions.assertNull(manager.getEpic(epic1.getId()));
        Assertions.assertTrue(manager.getAllSubtask().isEmpty());
    }

    @Test
    public void removeSubtaskByIdTest() {
        manager.createEpic(epic1);
        manager.createSubtask(subtask1);
        Epic epic = manager.getEpic(epic1.getId());

        manager.removeSubtaskById(subtask1.getId());

        Assertions.assertTrue(epic.getSubtasks().isEmpty());
        Assertions.assertNull(manager.getSubtask(subtask1.getId()));
    }

    @Test
    public void getSubtaskByEpicTest() {
        List<Integer> subtaskIds = List.of(2, 3);
        manager.createEpic(epic1);
        manager.createSubtask(subtask1);
        manager.createSubtask(subtask2);

        Epic epic = manager.getEpic(epic1.getId());

        for (int i = 0; i < epic.getSubtasks().size(); i++) {
            Assertions.assertTrue(subtaskIds.contains(epic.getSubtasks().get(i).getId()));
        }
    }

    @Test
    public void getHistoryTest() {
        manager.createEpic(epic1);
        manager.createSubtask(subtask1);
        manager.createSubtask(subtask2);
        manager.createTask(task1);

        manager.getTask(task1.getId());
        manager.getSubtask(subtask1.getId());
        manager.getSubtask(subtask2.getId());
        manager.getEpic(epic1.getId());

        Assertions.assertEquals(manager.getHistory().size(), 4);
    }
}
