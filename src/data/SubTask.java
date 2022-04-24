package data;

import java.time.Duration;
import java.time.LocalDateTime;

public class SubTask extends Task {
    private final long epicID;

    public SubTask(String title, String description, Status status, long epicID) {
        super(title, description, status);
        this.epicID = epicID;
    }

    public SubTask(long id, String title, String description, Status status, long epicID) {
        super(id, title, description, status);
        this.epicID = epicID;
    }

    public SubTask(String title, String description, Status status, LocalDateTime startTime,
                   Duration duration, long epicID) {
        super(title, description, status, startTime, duration);
        this.epicID = epicID;
    }

    public SubTask(long id, String title, String description, Status status, LocalDateTime startTime,
                   Duration duration, long epicID) {
        super(id, title, description, status, startTime, duration);
        this.epicID = epicID;
    }

    public long getEpicID() {
        return epicID;
    }

    @Override
    public String toString() {
        return  super.toString() +
                "SubTask{" +
                "epicID=" + epicID +
                '}';
    }

    @Override
    public String taskToString() {
        if (startTime.isEmpty()) {
            return String.format("%s,%s,%s,%s,%s,%s\n", super.getId(), TaskType.SUBTASK,
                    super.getTitle(), super.getStatus(), super.getDescription(), this.epicID);
        } else {
            return String.format("%s,%s,%s,%s,%s,%s,%s,%s\n", super.getId(), TaskType.SUBTASK,
                    super.getTitle(), super.getStatus(), super.getDescription(),
                    this.epicID, startTime.get().format(formatter), duration.toString());
        }
    }
}
