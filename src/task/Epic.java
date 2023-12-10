package task;

import manager.task.TaskType;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Epic extends Task {

    private LocalDateTime endTime;
    private List<Subtask> subtasks;

    public Epic(String name, String description) {
        super(name, description, Duration.ofSeconds(0));
        startTime = null;
        subtasks = new ArrayList<>();
    }

    public Epic(String name, String description, int id, TaskStatus status) {
        super(name, description, id, status, Duration.ofSeconds(0));
        subtasks = new ArrayList<>();
    }

    public Epic(String name, String description, int id, TaskStatus status, LocalDateTime statTime, Duration duration,
                LocalDateTime endTime) {
        super(name, description, id, status, statTime, duration);
        this.endTime = endTime;
    }

    public List<Subtask> getSubtasks() {
        return subtasks;
    }

    public void removeSubtask(Subtask subtask) {
        subtasks.remove(subtask);
        updateStatus();
        calculateStartTime();
        calculateEndTime();
    }

    public void addSubtask(Subtask subtask) {
        if (subtasks.contains(subtask)) {
            System.out.println("Такой subtask уже существует");
        } else {
            subtask.setStatus(TaskStatus.NEW);
            subtasks.add(subtask);
            updateStatus();
            calculateStartTime();
            calculateEndTime();
        }
    }

    public void removeAllSubtask() {
        subtasks.clear();
        setStatus(TaskStatus.NEW);
        startTime = null;
        duration = null;
    }

    public void changeSubtask(Subtask newSubtask) {
        for (Subtask subtask : subtasks) {
            if (subtask.getId() == newSubtask.getId()) {
                subtasks.remove(subtask);
                subtasks.add(newSubtask);
                updateStatus();
                break;
            }
        }
    }

    private void updateStatus() {
        int newCount = 0;
        int doneCount = 0;
        int inProgressCount = 0;

        for (Subtask subtask : subtasks) {
            switch (subtask.getStatus()) {
                case NEW: {
                    newCount++;
                    break;
                }
                case IN_PROGRESS: {
                    inProgressCount++;
                    break;
                }
                case DONE: {
                    doneCount++;
                    break;
                }
            }
        }

        if ((inProgressCount == 0 && doneCount == 0) || subtasks.isEmpty()) {
            setStatus(TaskStatus.NEW);
        } else if (inProgressCount == 0 && newCount == 0) {
            setStatus(TaskStatus.DONE);
        } else {
            setStatus(TaskStatus.IN_PROGRESS);
        }
    }

    private void calculateStartTime() {
        for (int i = 0; i < subtasks.size(); i++) {
            startTime = subtasks.get(i).startTime;
            if (startTime.isBefore(subtasks.get(i).startTime)) {
                startTime = subtasks.get(i + 1).startTime;
            }
        }
    }

    private void calculateEndTime() {
        for (int i = 0; i < subtasks.size(); i++) {
            duration = duration.plus(subtasks.get(i).duration);
        }
        endTime = startTime.plus(duration);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Epic epic = (Epic) o;
        return Objects.equals(subtasks, epic.subtasks) && super.equals(o);
    }

    @Override
    public int hashCode() {
        return subtasks != null ? subtasks.hashCode() : 0;
    }

    @Override
    public String taskToString() {
        return String.format("%d,%s,%s,%s,%s,%s,%s,%s", getId(), TaskType.EPIC.name(), getName(), getStatus(),
                getDescription(), startTime, duration, endTime);
    }

    @Override
    public String toString() {
        return "Epic{" +
                "name='" + getName() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", status=" + getStatus() +
                '}';
    }
}
