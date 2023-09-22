package task;

import java.util.ArrayList;
import java.util.Objects;

public class Epic extends Task {

    private ArrayList<Subtask> subtasks;

    public Epic(String name, String description) {
        super(name, description);
        subtasks = new ArrayList<>();
    }

    public ArrayList<Subtask> getSubtasks() {
        return subtasks;
    }

    public void removeSubtask(Subtask subtask) {
        subtasks.remove(subtask);
        validateStatus();
    }

    public void addSubtask(Subtask subtask) {
        if (subtasks.contains(subtask)) {
            System.out.println("Такой subtask уже существует");
        } else {
            subtask.setStatus(TaskStatus.NEW);
            subtasks.add(subtask);
        }
    }

    public void removeAllSubtask() {
        subtasks.clear();
        setStatus(TaskStatus.NEW);
    }

    public void changeSubtask(Subtask newSubtask, Subtask oldSubtask) {
        subtasks.remove(oldSubtask);
        subtasks.add(newSubtask);
        validateStatus();
    }

    private void validateStatus() {
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

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Epic epic = (Epic) o;
        return Objects.equals(subtasks, epic.subtasks) && Objects.equals(getId(),epic.getId());
    }

    @Override
    public int hashCode() {
        return subtasks != null ? subtasks.hashCode() : 0;
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
