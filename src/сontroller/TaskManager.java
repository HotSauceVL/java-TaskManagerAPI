package сontroller;

import tasks.Epic;
import tasks.SubTask;
import tasks.Task;
import java.util.List;

public interface TaskManager {
    int createTask(Task newTask);
    int createEpic(Epic newEpic);
    int createSubTask(SubTask newSubTask);
    void updateTask (int id, Task newTask);
    void updateEpic(int id, Epic newEpic);
    void updateSubTask(int id, SubTask newSubTask);
    List<Task> getTaskList();
    List<Epic> getEpicList();
    List<SubTask> getSubTaskList();
    void deleteAllTask();
    void deleteAllEpic();
    void deleteAllSubTask();
    void deleteByID(int id);
    Task getByID(int id);
    List<SubTask> getEpicSubTasks(int epicID);
}
