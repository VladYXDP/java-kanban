package task;

import java.util.ArrayList;

public class Epic extends Task {

    ArrayList<Subtask> subtasks;

    public Epic(String name, String description, int id) {
        super(name, description, id);
        subtasks = new ArrayList<>();
    }

    public ArrayList<Subtask> getSubtasks() {
        return subtasks;
    }

    public void removeSubtask(Subtask subtask) {
        subtasks.remove(subtask);
        validateStatus();
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
    public String toString() {
        return "Epic{" +
                "name='" + getName() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", status=" + getStatus() +
                '}';
    }
}
