package сontroller;

import tasks.Task;

import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    private static final List<Task> history = new ArrayList<>();
    @Override
    public void add(Task task) {
        if (history.size() < 11) {
            history.add(task);
        } else {
            history.remove(0);
            history.add(task);
        }
    }

    @Override
    public List<Task> getHistory() {
        return history;
    }

    @Override
    public void remove(Task task) {
        while (history.contains(task))
            history.remove(task);
    }

    @Override
    public void update(int id, Task newTask) {
        for (int i = 0; i < history.size(); i++) {
            if (history.get(i).getId() == id) {
                history.remove(i);
                history.add(i, newTask);
            }
        }
    }
}
