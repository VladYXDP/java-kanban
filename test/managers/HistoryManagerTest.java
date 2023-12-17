package managers;

import manager.Managers;
import manager.history.HistoryManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import task.Task;
import task.TaskStatus;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

public class HistoryManagerTest {

    @Test
    public void getEmptyHistory() {
        HistoryManager historyManager = Managers.getDefaultHistory();
        Assertions.assertTrue(historyManager.getHistory().isEmpty(), "История не пустая");
    }

    @Test
    public void checkRepeatTasks() {
        List<Integer> taskIds = List.of(2,1);
        HistoryManager historyManager = Managers.getDefaultHistory();
        historyManager.addTask(new Task("name", "desc", 1, TaskStatus.NEW,
                LocalDateTime.parse("2024-01-02T18:44:06.456050"), Duration.ofHours(0)));
        Assertions.assertEquals(historyManager.getHistory().size(), 1, "История задач должна быть равна 1");
        historyManager.addTask(new Task("name1", "desc1", 2, TaskStatus.NEW,
                LocalDateTime.parse("2024-01-03T18:44:06.456050"), Duration.ofHours(0)));
        Assertions.assertEquals(historyManager.getHistory().size(), 2, "История задач должна быть равна 2");
        historyManager.addTask(new Task("name", "desc", 1, TaskStatus.NEW,
                LocalDateTime.parse("2024-01-02T18:44:06.456050"), Duration.ofHours(0)));
        Assertions.assertEquals(historyManager.getHistory().size(), 2, "История задач должна быть равна 2");

        for(int i = 0; i < historyManager.getHistory().size(); i++){
            Assertions.assertEquals(taskIds.get(i), historyManager.getHistory().get(i).getId());
        }
    }

    @Test
    public void checkRemoveFromHead() {
        List<Integer> taskIds = List.of(2,3);
        HistoryManager historyManager = Managers.getDefaultHistory();
        historyManager.addTask(new Task("name", "desc", 1, TaskStatus.NEW,
                LocalDateTime.parse("2024-01-02T18:44:06.456050"), Duration.ofHours(0)));
        Assertions.assertEquals(historyManager.getHistory().size(), 1, "История задач должна быть равна 1");
        historyManager.addTask(new Task("name1", "desc1", 2, TaskStatus.NEW,
                LocalDateTime.parse("2024-01-03T18:44:06.456050"), Duration.ofHours(0)));
        Assertions.assertEquals(historyManager.getHistory().size(), 2, "История задач должна быть равна 2");
        historyManager.addTask(new Task("name", "desc", 3, TaskStatus.NEW,
                LocalDateTime.parse("2024-01-02T18:44:06.456050"), Duration.ofHours(0)));
        Assertions.assertEquals(historyManager.getHistory().size(), 3, "История задач должна быть равна 3");

        historyManager.remove(1);

        for(int i = 0; i < historyManager.getHistory().size(); i++){
            Assertions.assertEquals(taskIds.get(i), historyManager.getHistory().get(i).getId());
        }
    }

    @Test
    public void checkRemoveFromMiddle() {
        List<Integer> taskIds = List.of(1,3);
        HistoryManager historyManager = Managers.getDefaultHistory();
        historyManager.addTask(new Task("name", "desc", 1, TaskStatus.NEW,
                LocalDateTime.parse("2024-01-02T18:44:06.456050"), Duration.ofHours(0)));
        Assertions.assertEquals(historyManager.getHistory().size(), 1, "История задач должна быть равна 1");
        historyManager.addTask(new Task("name1", "desc1", 2, TaskStatus.NEW,
                LocalDateTime.parse("2024-01-03T18:44:06.456050"), Duration.ofHours(0)));
        Assertions.assertEquals(historyManager.getHistory().size(), 2, "История задач должна быть равна 2");
        historyManager.addTask(new Task("name", "desc", 3, TaskStatus.NEW,
                LocalDateTime.parse("2024-01-02T18:44:06.456050"), Duration.ofHours(0)));
        Assertions.assertEquals(historyManager.getHistory().size(), 3, "История задач должна быть равна 2");

        historyManager.remove(2);

        for(int i = 0; i < historyManager.getHistory().size(); i++){
            Assertions.assertEquals(taskIds.get(i), historyManager.getHistory().get(i).getId());
        }
    }

    @Test
    public void checkRemoveFromTail() {
        List<Integer> taskIds = List.of(1,2);
        HistoryManager historyManager = Managers.getDefaultHistory();
        historyManager.addTask(new Task("name", "desc", 1, TaskStatus.NEW,
                LocalDateTime.parse("2024-01-02T18:44:06.456050"), Duration.ofHours(0)));
        Assertions.assertEquals(historyManager.getHistory().size(), 1, "История задач должна быть равна 1");
        historyManager.addTask(new Task("name1", "desc1", 2, TaskStatus.NEW,
                LocalDateTime.parse("2024-01-03T18:44:06.456050"), Duration.ofHours(0)));
        Assertions.assertEquals(historyManager.getHistory().size(), 2, "История задач должна быть равна 2");
        historyManager.addTask(new Task("name", "desc", 3, TaskStatus.NEW,
                LocalDateTime.parse("2024-01-02T18:44:06.456050"), Duration.ofHours(0)));
        Assertions.assertEquals(historyManager.getHistory().size(), 3, "История задач должна быть равна 2");

        historyManager.remove(3);

        for(int i = 0; i < historyManager.getHistory().size(); i++){
            Assertions.assertEquals(taskIds.get(i), historyManager.getHistory().get(i).getId());
        }
    }
}
