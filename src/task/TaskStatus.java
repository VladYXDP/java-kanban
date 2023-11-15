package task;

public enum TaskStatus {
    NEW,
    IN_PROGRESS,
    DONE;

    public static TaskStatus getTaskStatusByString(String status) {
        for(TaskStatus taskStatus: values()) {
            if(taskStatus.name().equals(status)) {
                return taskStatus;
            }
        }
        return null;
    }
}
