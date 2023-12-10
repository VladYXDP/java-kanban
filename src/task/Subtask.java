package task;

import manager.task.TaskType;

import java.time.Duration;
import java.time.LocalDateTime;

public class Subtask extends Task {

    private int epicId;

    public Subtask(String name, String description, int epicId, LocalDateTime starTime, Duration duration) {
        super(name, description, starTime, duration);
        this.epicId = epicId;
    }

    public Subtask(String name, String description, int id, TaskStatus status, int epicId, Duration duration) {
        super(name, description, id, status, duration);
        this.epicId = epicId;
    }

    public Subtask(String name, String description, int id, TaskStatus status, int epicId, LocalDateTime startTime,
                   Duration duration) {
        super(name, description, id, status, startTime, duration);
        this.epicId = epicId;
    }

    public int getEpicId() {
        return epicId;
    }

    public void setEpicId(int epicId) {
        this.epicId = epicId;
    }

    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public String taskToString() {
        return String.format("%d,%s,%s,%s,%s,%s,%s,%s,%s", getId(), TaskType.SUBTASK.name(), getName(), getStatus(),
                getDescription(), getEpicId(), startTime, duration, getEndTime());
    }

    @Override
    public String toString() {
        return "Subtask{" +
                "name='" + getName() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", status=" + getStatus() +
                '}';
    }
}
