import manager.Managers;
import manager.task.TaskManager;
import task.Epic;
import task.Subtask;
import task.Task;

public class Main {

    public static void main(String[] args) {
        TaskManager taskManager = Managers.getDefault();

        System.out.println(taskManager.getHistory());

        Task task1 = new Task("Task1", "Desc task1");
        Task task2 = new Task("Task2", "Desc task2");
        Task task3 = new Task("Task3", "Desc task3");

        Epic epic1 = new Epic("Epic1", "Desc epic1");
        taskManager.createEpic(epic1);
        Subtask subtask1 = new Subtask("Subtask1", "Desc subtask1", epic1.getId());
        Subtask subtask2 = new Subtask("Subtask2", "Desc subtask2", epic1.getId());

        Epic epic2 = new Epic("Epic2", "Desc epic2");
        taskManager.createEpic(epic2);
        Subtask subtask3 = new Subtask("Subtask3", "Desc subtask3", epic2.getId());

        taskManager.createTask(task1);
        taskManager.createTask(task2);
        taskManager.createTask(task3);
        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);
        taskManager.createSubtask(subtask3);

        taskManager.getTask(3);
        System.out.println(taskManager.getHistory());
        taskManager.getTask(4);
        taskManager.getTask(5);
        System.out.println(taskManager.getHistory());
        taskManager.removeTaskById(5);
        System.out.println(taskManager.getHistory());
//        taskManager.getTask(5);
//        System.out.println(taskManager.getHistory());
//        taskManager.getTask(4);
//        System.out.println(taskManager.getHistory());
//        taskManager.getTask(3);
//        System.out.println(taskManager.getHistory());
//        taskManager.getTask(4);
//        System.out.println(taskManager.getHistory());
    }
}
