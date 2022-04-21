package data;

import сontroller.InMemoryTaskManager;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class Epic extends Task {
    private final ArrayList<Long> subTasks = new ArrayList<>();
    private LocalDateTime endTime;

    public Epic(String title, String description, Status status) {
        super(title, description, status);
        super.startTime = LocalDateTime.now();
        this.endTime = LocalDateTime.now();
        super.duration = Duration.ZERO;
    }

    public Epic(long id, String title, String description, Status status) {
        super(id, title, description, status);
        super.startTime = LocalDateTime.now();
        this.endTime = LocalDateTime.now();
        super.duration = Duration.ZERO;
    }

    public ArrayList<Long> getSubTasks() {
        return subTasks;
    }

    public void addSubTask(long subTaskID) {
        subTasks.add(subTaskID);
    }

    public void deleteSubTask(long subTaskID) {
        subTasks.remove(subTaskID);
    }

    public void clearSubTasks () {
        subTasks.clear();
    }

    @Override
    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    @Override
    public String toString() {
        return  super.toString() +
                "Epic{" +
                "subTasks=" + subTasks +
                '}';
    }

    @Override
    public String taskToString() {
        return String.format("%s,%s,%s,%s,%s\n", super.getId(), TaskType.EPIC, super.getTitle(), super.getStatus(),
                super.getDescription());
    }
}
