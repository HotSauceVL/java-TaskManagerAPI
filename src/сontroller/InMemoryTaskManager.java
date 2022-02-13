package сontroller;

import tasks.*;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Map;
import java.util.List;

public class InMemoryTaskManager implements TaskManager {
    private static int taskID = 0;
    private static final Map<Integer, Task> task = new HashMap<>();
    private static final Map<Integer, Epic> epic = new HashMap<>();
    private static final Map<Integer, SubTask> subTask = new HashMap<>();
    private static final HistoryManager historyManager = new InMemoryHistoryManager();
    private final Task nullTask = new Task("Ошибка", "Такого ID не существует", Status.ERROR);

    int getNewID() {
        return ++taskID;
    }

    @Override
    public int createTask(Task newTask) {
        task.put(getNewID(), newTask);
        newTask.setId(taskID);
        return newTask.getId();
    }

    @Override
    public int createEpic(Epic newEpic) {
        epic.put(getNewID(), newEpic);
        newEpic.setId(taskID);
        newEpic.setStatus(Status.NEW);
        return newEpic.getId();
    }

    @Override
    public int createSubTask(SubTask newSubTask) {
        if (epic.containsKey(newSubTask.getEpicID())) {
            subTask.put(getNewID(), newSubTask);
            newSubTask.setId(taskID);
            epic.get(newSubTask.getEpicID()).addSubTasks(taskID);
            updateEpicStatus(newSubTask.getEpicID());
            return newSubTask.getId();
        } else {
            System.out.println("Нельзя добавить подзадачу для несуществующего эпика");
            return -1;
        }
    }

    @Override
    public void updateTask (int id, Task newTask) {
        if (task.containsKey(id)) {
            task.put(id, newTask);
            newTask.setId(id);
            historyManager.update(id, newTask);
        } else {
            System.out.println("Нет задачи с таким ID");
            return;
        }
    }

    @Override
    public void updateEpic(int id, Epic newEpic) {
        if (epic.containsKey(id)) {
            historyManager.update(id, newEpic);
            epic.put(id, newEpic);
            newEpic.setId(id);
            updateEpicStatus(id);
            for (SubTask subTaskObject : subTask.values()) {
                if (subTaskObject.getEpicID() == id) {
                    newEpic.addSubTasks(subTaskObject.getId());
                }
            }
        } else {
            System.out.println("Нет эпика с таким ID");
            return;
        }
    }

    @Override
    public void updateSubTask(int id, SubTask newSubTask) {
        if (subTask.containsKey(id)) {
            if (epic.containsKey(newSubTask.getEpicID())) {
                if (subTask.get(id).getEpicID() != newSubTask.getEpicID()) {
                    epic.get(subTask.get(id).getEpicID()).deleteSubTask(id);
                    epic.get(newSubTask.getEpicID()).addSubTasks(id);
                    updateEpicStatus(subTask.get(id).getEpicID());
                }
                historyManager.update(id, newSubTask);
                subTask.put(id, newSubTask);
                newSubTask.setId(id);
                updateEpicStatus(newSubTask.getEpicID());
            } else {
                System.out.println("Нельзя добавить подзадачу для несуществующего эпика");
            }
        } else {
            System.out.println("Нет подзадачи с таким ID");
            return;
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
    public void deleteByID(int id) {
        if (task.containsKey(id) || epic.containsKey(id) || subTask.containsKey(id)) {
            if (task.containsKey(id)) {
                historyManager.remove(task.get(id));
                task.remove(id);
            } else if (epic.containsKey(id)) {
                ArrayList<Integer> subTasksID = epic.get(id).getSubTasks();
                for (Integer subTaskID : subTasksID) {
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
            System.out.println("Ошибка! Такого ID не существует");
            return;
        }
    }

    @Override
    public Task getByID(int id) {
        if (task.containsKey(id) || epic.containsKey(id) || subTask.containsKey(id)) {
            Task container = null;

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
            System.out.println("Ошибка! Такого ID не существует");
            return nullTask;
        }
    }

    @Override
    public List<SubTask> getEpicSubTasks (int epicID) {
        if (epic.containsKey(epicID)) {
            List<Integer> subTasksID = epic.get(epicID).getSubTasks();
            List<SubTask> epicSubTasks = new ArrayList<>();
            for (Integer subTaskID : subTasksID) {
                epicSubTasks.add(subTask.get(subTaskID));
            }
            return epicSubTasks;
        } else {
            System.out.println("Ошибка! Эпика с таким ID не существует");
            List<SubTask> epicSubTasks = new ArrayList<>();
            epicSubTasks.add(new SubTask("Ошибка", "Эпика с таким ID не существует",
                    Status.ERROR, -1));
            return epicSubTasks;
        }

    }

    private void updateEpicStatus(int epicID) {
        ArrayList<Integer> subTasksID = epic.get(epicID).getSubTasks();

        if (subTasksID.size() == 0) {
            epic.get(epicID).setStatus(Status.NEW);
            return;
        }
        ArrayList<Status> statusList = new ArrayList<>();
        for (Integer subTaskID : subTasksID) {
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
}
