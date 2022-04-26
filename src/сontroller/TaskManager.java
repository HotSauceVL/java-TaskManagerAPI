package сontroller;

import data.Epic;
import data.SubTask;
import data.Task;

import java.util.Collection;
import java.util.List;

public interface TaskManager {
    long createTask(Task newTask);
    long createEpic(Epic newEpic);
    long createSubTask(SubTask newSubTask);
    void updateTask (long id, Task newTask);
    void updateEpic(long id, Epic newEpic);
    void updateSubTask(long id, SubTask newSubTask);
    List<Task> getTaskList();
    List<Epic> getEpicList();
    List<SubTask> getSubTaskList();
    void deleteAllTask();
    void deleteAllEpic();
    void deleteAllSubTask();
    void deleteByID(long id);
    Task getByID(long id);
    List<SubTask> getEpicSubTasks(long epicID);
    List<Task> history();
    Collection<Task> getPrioritizedTasks();
}
