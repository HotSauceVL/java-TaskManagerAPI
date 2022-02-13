package сontroller;

import tasks.Task;

import java.util.List;

public interface HistoryManager {
    void add(Task task);
    List<Task> getHistory();
    void remove(Task task);
    void update(int id, Task newTask);
}
