package data;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Objects;
import java.util.Optional;

public class Task {
    protected String title;
    protected String description;
    protected Status status;
    protected long id;
    protected Duration duration;
    protected Optional<LocalDateTime> startTime;
    protected static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy: HH.mm");

    public Task (String title, String description, Status status) {
        this.title = title;
        this.description = description;
        this.status = status;
        this.startTime = Optional.empty();
        this.duration = Duration.ZERO;
    }

    public Task (long id, String title, String description, Status status) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.status = status;
        this.startTime = Optional.empty();
        this.duration = Duration.ZERO;
    }
    public Task(String title, String description, Status status, LocalDateTime startTime, Duration duration) {
        this.title = title;
        this.description = description;
        this.status = status;
        this.startTime = Optional.of(startTime);
        this.duration = duration;
    }

    public Task(long id, String title, String description, Status status, LocalDateTime startTime, Duration duration) {
        this.id = id;// появился вопрос, насколько корректен с точки зрения Инкапсуляции
        // конструктор с возможностью задать id?
        this.title = title;
        this.description = description;
        this.status = status;
        this.startTime = Optional.of(startTime);
        this.duration = duration;
    }

    public static DateTimeFormatter getFormatter() {
        return formatter;
    }

    public Duration getDuration() {
        return duration;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public Optional<LocalDateTime> getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = Optional.of(startTime);
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public Optional<LocalDateTime> getEndTime() {
        return startTime.map(localDateTime -> localDateTime.plus(duration));
    }

    @Override
    public String toString() {
        if (startTime.isPresent())
        return "Task{" +
                "title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", status='" + status + '\'' +
                ", id=" + id + '\'' +
                ", StartTime='" + startTime.get().format(formatter) + '\'' +
                ", EndTime='" + getEndTime().get().format(formatter) + '\'' +
                '}';
        else
            return "Task{" +
                    "title='" + title + '\'' +
                    ", description='" + description + '\'' +
                    ", status='" + status + '\'' +
                    ", id=" + id +
                    '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return id == task.id && Objects.equals(title, task.title) && Objects.equals(description, task.description)
                && status == task.status;
    }

    public String taskToString() {
        if (startTime.isEmpty()) {
            return String.format("%s,%s,%s,%s,%s\n", id, TaskType.TASK, title, status, description);
        } else {
            return String.format("%s,%s,%s,%s,%s,%s,%s\n", id, TaskType.TASK, title, status, description,
                    startTime.get().format(formatter), duration.toString());
        }
    }
}
