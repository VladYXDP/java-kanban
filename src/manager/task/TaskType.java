package manager.task;

public enum TaskType {
    TASK,
    EPIC,
    SUBTASK;

    public static TaskType getTaskTypeByString(String type) {
        for (TaskType taskType : values()) {
            if (taskType.name().equals(type)) {
                return taskType;
            }
        }
        return null;
    }
}
