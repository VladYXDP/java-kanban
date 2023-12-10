//package test;
//
//import manager.task.InMemoryTaskManager;
//import org.junit.Test;
//import org.junit.jupiter.api.Assertions;
//import task.Epic;
//import task.Subtask;
//import task.Task;
//import task.TaskStatus;
//
//public class InMemoryTaskTest extends TaskManagerTest<InMemoryTaskManager> {
//
//    @Test
//    public void checkEpicAndSubtask() {
//        setManager(new InMemoryTaskManager());
//        int epicId = addNewEpic(new Epic("Epic Name", "Epic Desc"));
//        Epic epic = getEpic(epicId);
//        Assertions.assertEquals(epic.getId(), epicId, "Разные индексы");
//        Assertions.assertEquals(getAllEpic().size(), 1, "Неверно заполняется список Эпиков");
//
//        int subtaskId1 = addNewSubtask(new Subtask("Subtask Name", "Subtask Desc", epicId));
//        int subtaskId2 = addNewSubtask(new Subtask("Subtask Name", "Subtask Desc", epicId));
//        Subtask subtask1 = getSubtask(subtaskId1);
//        Subtask subtask2 = getSubtask(subtaskId2);
//        Assertions.assertEquals(subtask1.getId(), subtaskId1);
//        Assertions.assertEquals(subtask1.getEpicId(), epicId);
//        Assertions.assertEquals(epic.getStatus().name(), TaskStatus.NEW.name(), "Неверный статус Эпика");
//        Assertions.assertEquals(getEpic(epicId).getSubtasks().size(), 2, "Неверно заполняется список подзадач Эпика");
//        Assertions.assertEquals(getAllSubtask().size(), 2, "Неверно заполняется список подзадач");
//
//        subtask1.setStatus(TaskStatus.DONE);
//        subtask2.setStatus(TaskStatus.DONE);
//        updateSubtask(getSubtask(subtaskId1));
//        updateSubtask(getSubtask(subtaskId2));
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
//
//
//    }
//
//    @Test
//    public void checkTask() {
//        setManager(new InMemoryTaskManager());
//        int globalTaskId = addNewTask(new Task("Task name", "Task desc"));
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
//    public void checkRemoving() {
//        setManager(new InMemoryTaskManager());
//        int taskId = addNewTask(new Task("TaskName", "TaskDesc"));
//        addNewTask(new Task("TaskName", "TaskDesc"));
//        int epicId = addNewEpic(new Epic("Epic Name", "Epic desc"));
//        int subtask = addNewSubtask(new Subtask("Subtask name", "Subtask desc", epicId));
//
//        removeTaskById(taskId);
//        Assertions.assertNull(getTask(taskId));
//
//    }
//}
