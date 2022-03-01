package сontroller;

import data.Task;

import java.util.List;

public interface HistoryManager {
    void add(Task task);
    List<Task> getHistory();
    void remove(Task task);
    void update(long id, Task newTask);
}
