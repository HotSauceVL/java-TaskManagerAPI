package сontroller;

import data.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Map;
import java.util.List;

public class InMemoryTaskManager implements TaskManager {
    private static long taskID = 0;
    protected static final Map<Long, Task> task = new HashMap<>();
    protected static final Map<Long, Epic> epic = new HashMap<>();
    protected static final Map<Long, SubTask> subTask = new HashMap<>();
    protected static final HistoryManager historyManager = new InMemoryHistoryManager();
    private final Task nullTask = new Task("Ошибка", "Такого ID не существует", Status.ERROR,
            LocalDateTime.now(),  Duration.between(LocalDateTime.now(), LocalDateTime.now()));

    long getNewID() {
        return ++taskID;
    }

    static long getTaskID() {
        return taskID;
    }

    static void setTaskID(long id) {
        taskID = id;
    }

    static void putTask(Task loadedTask) {
        task.put(loadedTask.getId(), loadedTask);
    }

    static void putEpic(Epic loadedEpic) {
        epic.put(loadedEpic.getId(), loadedEpic);
    }

    static void putSubTask(SubTask loadedSubTask) {
        subTask.put(loadedSubTask.getId(), loadedSubTask);
    }

    @Override
    public long createTask(Task newTask) {
        task.put(getNewID(), newTask);
        newTask.setId(taskID);
        return newTask.getId();
    }

    @Override
    public long createEpic(Epic newEpic) {
        epic.put(getNewID(), newEpic);
        newEpic.setId(taskID);
        newEpic.setStatus(Status.NEW);
        return newEpic.getId();
    }

    @Override
    public long createSubTask(SubTask newSubTask) {
        if (epic.containsKey(newSubTask.getEpicID())) {
            subTask.put(getNewID(), newSubTask);
            newSubTask.setId(taskID);
            epic.get(newSubTask.getEpicID()).addSubTask(taskID);
            updateEpicStatus(newSubTask.getEpicID());
            updateEpicStartAndEndTime(newSubTask.getEpicID());
            return newSubTask.getId();
        } else {
            throw new IllegalArgumentException ("Нельзя добавить подзадачу для несуществующего эпика");
        }
    }

    @Override
    public void updateTask (long id, Task newTask) {
        if (task.containsKey(id)) {
            task.put(id, newTask);
            newTask.setId(id);
            InMemoryHistoryManager.update(id, newTask);
        } else {
            throw new IllegalArgumentException ("Нет задачи с таким ID");
        }
    }

    @Override
    public void updateEpic(long id, Epic newEpic) {
        if (epic.containsKey(id)) {
            InMemoryHistoryManager.update(id, newEpic);
            epic.put(id, newEpic);
            newEpic.setId(id);
            for (SubTask subTaskObject : subTask.values()) {
                if (subTaskObject.getEpicID() == id) {
                    newEpic.addSubTask(subTaskObject.getId());
                }
            }
            updateEpicStatus(id);
            updateEpicStartAndEndTime(id);
        } else {
            throw new IllegalArgumentException ("Нет эпика с таким ID");
        }
    }

    @Override
    public void updateSubTask(long id, SubTask newSubTask) {
        if (subTask.containsKey(id)) {
            if (epic.containsKey(newSubTask.getEpicID())) {
                if (subTask.get(id).getEpicID() != newSubTask.getEpicID()) {
                    epic.get(subTask.get(id).getEpicID()).deleteSubTask(id);
                    epic.get(newSubTask.getEpicID()).addSubTask(id);
                    updateEpicStatus(subTask.get(id).getEpicID());
                    updateEpicStartAndEndTime(subTask.get(id).getEpicID());
                }
                InMemoryHistoryManager.update(id, newSubTask);
                subTask.put(id, newSubTask);
                newSubTask.setId(id);
                updateEpicStatus(newSubTask.getEpicID());
                updateEpicStartAndEndTime(subTask.get(id).getEpicID());
            } else {
                throw new IllegalArgumentException("Нельзя добавить подзадачу для несуществующего эпика");
            }
        } else {
            throw new IllegalArgumentException("Нет подзадачи с таким ID");
        }
    }

    @Override
    public List<Task> getTaskList() {
        List<Task> taskList = new ArrayList<>();
        taskList.addAll(task.values());
        return taskList;
    }

    @Override
    public List<Epic> getEpicList() {
        List<Epic> epicList = new ArrayList<>();
        epicList.addAll(epic.values());
        return epicList;
    }

    @Override
    public List<SubTask> getSubTaskList() {
        List<SubTask> subTaskList = new ArrayList<>();
        subTaskList.addAll(subTask.values());
        return subTaskList;
    }

    @Override
    public void deleteAllTask() {
        for (Task taskToRemove : task.values()) {
            historyManager.remove(taskToRemove);
        }
        task.clear();
    }

    @Override
    public void deleteAllEpic() {
        for (Epic epicToRemove : epic.values()) {
            historyManager.remove(epicToRemove);
        }
        epic.clear();
        for (SubTask subTaskToRemove : subTask.values()) {
            historyManager.remove(subTaskToRemove);
        }
        subTask.clear();
    }

    @Override
    public void deleteAllSubTask() {
        for (SubTask subTaskToRemove : subTask.values()) {
            historyManager.remove(subTaskToRemove);
        }
        subTask.clear();
        for (Epic epicObject : epic.values()) {
            epicObject.setStatus(Status.NEW);
            epicObject.clearSubTasks();
        }
    }

    @Override
    public void deleteByID(long id) {
        if (task.containsKey(id) || epic.containsKey(id) || subTask.containsKey(id)) {
            if (task.containsKey(id)) {
                historyManager.remove(task.get(id));
                task.remove(id);
            } else if (epic.containsKey(id)) {
                ArrayList<Long> subTasksID = epic.get(id).getSubTasks();
                for (Long subTaskID : subTasksID) {
                    historyManager.remove(subTask.get(subTaskID));
                    subTask.remove(subTaskID);
                }
                historyManager.remove(epic.get(id));
                epic.remove(id);
            } else if (subTask.containsKey(id)) {
                epic.get(subTask.get(id).getEpicID()).deleteSubTask(id);
                updateEpicStatus(subTask.get(id).getEpicID());
                historyManager.remove(subTask.get(id));
                subTask.remove(id);
            }
        } else {
            throw new IllegalArgumentException ("Такого ID не существует");
        }
    }

    @Override
    public Task getByID(long id) {
        if (task.containsKey(id) || epic.containsKey(id) || subTask.containsKey(id)) {
            Task container = nullTask;

            if (task.containsKey(id)) {
                container = task.get(id);
            } else if (epic.containsKey(id)) {
                container = epic.get(id);
            } else if (subTask.containsKey(id)) {
                container = subTask.get(id);
            }
            historyManager.add(container);
            return container;
        } else {
            throw new IllegalArgumentException("Такого ID не существует");
        }
    }

    @Override
    public List<SubTask> getEpicSubTasks (long epicID) {
        if (epic.containsKey(epicID)) {
            List<Long> subTasksID = epic.get(epicID).getSubTasks();
            List<SubTask> epicSubTasks = new ArrayList<>();
            for (Long subTaskID : subTasksID) {
                epicSubTasks.add(subTask.get(subTaskID));
            }
            return epicSubTasks;
        } else {
            throw new IllegalArgumentException("Эпика с таким ID не существует");
        }

    }

    private void updateEpicStatus(long epicID) {
        ArrayList<Long> subTasksID = epic.get(epicID).getSubTasks();

        if (subTasksID.size() == 0) {
            epic.get(epicID).setStatus(Status.NEW);
            return;
        }
        ArrayList<Status> statusList = new ArrayList<>();
        for (Long subTaskID : subTasksID) {
            statusList.add(subTask.get(subTaskID).getStatus());
        }
        if (statusList.contains(Status.NEW) && !statusList.contains(Status.IN_PROGRESS)
                && !statusList.contains(Status.DONE)) {
            epic.get(epicID).setStatus(Status.NEW);
        } else if (statusList.contains(Status.DONE) && !statusList.contains(Status.IN_PROGRESS)
                && !statusList.contains(Status.NEW)) {
            epic.get(epicID).setStatus(Status.DONE);
        } else
            epic.get(epicID).setStatus(Status.IN_PROGRESS);
    }

    private void updateEpicStartAndEndTime(long epicID) {
        ArrayList<Long> subTasksID = epic.get(epicID).getSubTasks();

        if (subTasksID.size() == 0) {
            return;
        }
        LocalDateTime startTime = LocalDateTime.MIN;
        LocalDateTime endTime = LocalDateTime.MIN;
        Duration epicDuration = Duration.ZERO;
        for (Long subTaskID : subTasksID) {
            if (subTask.get(subTaskID).getStartTime() != null) {
                if (startTime.isAfter(subTask.get(subTaskID).getStartTime())) {
                    startTime = subTask.get(subTaskID).getStartTime();
                }
                if (endTime.isBefore(subTask.get(subTaskID).getEndTime())) {
                    endTime = subTask.get(subTaskID).getEndTime();
                }
                epicDuration.plus(subTask.get(subTaskID).getDuration());
            }
        }
        if (startTime == LocalDateTime.MIN) {
            return;
        }
        epic.get(epicID).setDuration(epicDuration);
        epic.get(epicID).setStartTime(startTime);
        epic.get(epicID).setEndTime(endTime);
    }

    @Override
    public List<Task> history() {
        return historyManager.getHistory();
    }
}
