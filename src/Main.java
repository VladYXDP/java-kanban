import manager.HistoryManager;
import manager.Managers;
import manager.TaskManager;
import task.Epic;
import task.Subtask;
import task.Task;
import task.TaskStatus;

public class Main {

    public static void main(String[] args) {
        Managers managers = new Managers();
        TaskManager taskManager = managers.getDefault();
        HistoryManager historyManager = Managers.getDefaultHistory();

        Task task1 = new Task("Task1", "Desc task1");
        Task task2 = new Task("Task2", "Desc task2");

        Epic epic1 = new Epic("Epic1", "Desc epic1");
        taskManager.createEpic(epic1);
        Subtask subtask1 = new Subtask("Subtask1", "Desc subtask1", epic1.getId());
        Subtask subtask2 = new Subtask("Subtask2", "Desc subtask2", epic1.getId());

        Epic epic2 = new Epic("Epic2", "Desc epic2");
        taskManager.createEpic(epic2);
        Subtask subtask3 = new Subtask("Subtask3", "Desc subtask3", epic2.getId());

        taskManager.createTask(task1);
        taskManager.createTask(task2);
        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);
        taskManager.createSubtask(subtask3);

        System.out.println(taskManager.getAllTask());
        System.out.println(taskManager.getAllEpic());
        System.out.println(taskManager.getAllSubtask());
        System.out.println("==========================================================================");

        task1.setStatus(TaskStatus.IN_PROGRESS);
        task2.setStatus(TaskStatus.DONE);

        epic1.setDescription("New Desc epic1");
        epic2.setDescription("New Desc epic2");
        subtask1.setName("New name subtask1");

        taskManager.updateTask(task1);
        taskManager.updateTask(task2);
        taskManager.updateEpic(epic1);
        taskManager.updateEpic(epic2);
        taskManager.updateSubtask(subtask1);

        System.out.println(taskManager.getAllTask());
        System.out.println(taskManager.getAllEpic());
        System.out.println(taskManager.getAllSubtask());

        System.out.println("==========================================================================");

        subtask1.setStatus(TaskStatus.DONE);
        taskManager.updateSubtask(subtask1);

        System.out.println(taskManager.getAllTask());
        System.out.println(taskManager.getAllEpic());
        System.out.println(taskManager.getAllSubtask());

        System.out.println("==========================================================================");

        taskManager.getSubtask(5);
        System.out.println(taskManager.getHistory());
        taskManager.getSubtask(5);
        taskManager.getSubtask(5);
        taskManager.getSubtask(5);
        taskManager.getSubtask(5);
        taskManager.getSubtask(5);
        System.out.println(taskManager.getHistory());
        taskManager.getSubtask(5);
        taskManager.getSubtask(5);
        taskManager.getSubtask(5);
        taskManager.getSubtask(5);
        taskManager.getSubtask(5);
        System.out.println(taskManager.getHistory());
        taskManager.getEpic(1);
        System.out.println(taskManager.getHistory());
    }
}
