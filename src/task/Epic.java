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


    public Epic(String name, String description, int id, TaskStatus status) {
        super(name, description, id, status);
        subtasks = new ArrayList<>();
    }

    public Epic(String name, String description, LocalDateTime startTime) {
        super(name, description, startTime, Duration.ofHours(0));
        subtasks = new ArrayList<>();
    }

    public Epic(String name, String description, int id, TaskStatus status, LocalDateTime statTime, Duration duration,
                LocalDateTime endTime) {
        super(name, description, id, status, statTime, duration);
        this.endTime = endTime;
        subtasks = new ArrayList<>();
    }

    public List<Subtask> getSubtasks() {
        return subtasks;
    }

    public void removeSubtask(Subtask subtask) {
        subtasks.remove(subtask);
        updateStatus();
        calculateStartTime();
        calculateEndTime();
        calculateDuration();
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
            calculateDuration();
        }
    }

    public void removeAllSubtask() {
        subtasks.clear();
        setStatus(TaskStatus.NEW);
        startTime = null;
        duration = null;
        endTime = null;
    }

    public void changeSubtask(Subtask newSubtask) {
        for (Subtask subtask : subtasks) {
            if (subtask.getId() == newSubtask.getId()) {
                subtasks.remove(subtask);
                subtasks.add(newSubtask);
                updateStatus();
                calculateStartTime();
                calculateEndTime();
                calculateDuration();
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
        startTime = null;
        for (Subtask subtask : subtasks) {
            if (startTime == null) {
                startTime = subtask.startTime;
                continue;
            }
            if (startTime.isAfter(subtask.startTime)) {
                startTime = subtask.startTime;
            }
        }
    }

    private void calculateDuration() {
        duration = Duration.ofHours(0);
        for (Subtask subtask: subtasks) {
            duration = duration.plus(subtask.duration);
        }
        if (duration.equals(Duration.ofHours(0))) {
            duration = null;
        }
    }

    private void calculateEndTime() {
        endTime = null;
        for (Subtask subtask : subtasks) {
            if (endTime == null) {
                endTime = subtask.getEndTime();
                continue;
            }
            if (endTime.isBefore(subtask.getEndTime())) {
                endTime = subtask.getEndTime();
            }
        }
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Epic epic = (Epic) o;
        return Objects.equals(subtasks, epic.subtasks);
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
