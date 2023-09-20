import task.Epic;
import task.Subtask;
import task.Task;
import task.TaskStatus;

public class Main {

    public static void main(String[] args) {
        Manager manager = new Manager();

        Task task1 = new Task("Task1", "Desc task1", manager.createTaskIndex());
        Task task2 = new Task("Task2", "Desc task2", manager.createTaskIndex());

        Epic epic1 = new Epic("Epic1", "Desc epic1", manager.createTaskIndex());
        Subtask subtask1 = new Subtask("Subtask1", "Desc subtask1", manager.createTaskIndex());
        Subtask subtask2 = new Subtask("Subtask2", "Desc subtask2", manager.createTaskIndex());

        Epic epic2 = new Epic("Epic2", "Desc epic2", manager.createTaskIndex());
        Subtask subtask3 = new Subtask("Subtask3", "Desc subtask3", manager.createTaskIndex());

        manager.createTask(task1);
        manager.createTask(task2);

        manager.createEpic(epic1);
        manager.createSubtask(subtask1, epic1);
        manager.createSubtask(subtask2, epic1);

        manager.createEpic(epic2);
        manager.createSubtask(subtask3, epic2);

        System.out.println(manager.getAllTask());
        System.out.println(manager.getAllEpic());
        System.out.println(manager.getAllSubtask());
        System.out.println("==========================================================================");

        task1.setStatus(TaskStatus.IN_PROGRESS);
        task2.setStatus(TaskStatus.DONE);

        epic1.setDescription("New Desc epic1");
        epic2.setDescription("New Desc epic2");
        subtask1.setName("New name subtask1");

        manager.updateTask(task1);
        manager.updateTask(task2);
        manager.updateEpic(epic1);
        manager.updateEpic(epic2);
        manager.updateSubtask(subtask1);

        System.out.println(manager.getAllTask());
        System.out.println(manager.getAllEpic());
        System.out.println(manager.getAllSubtask());

        System.out.println("==========================================================================");

        subtask1.setStatus(TaskStatus.DONE);
        manager.updateSubtask(subtask1);

        System.out.println(manager.getAllTask());
        System.out.println(manager.getAllEpic());
        System.out.println(manager.getAllSubtask());
    }
}
