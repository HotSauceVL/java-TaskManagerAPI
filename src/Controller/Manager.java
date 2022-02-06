package Controller;

import Tasks.*;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Map;

public class Manager {
    private static int taskID = 0;
    private static final Map<Integer, Task> task = new HashMap<>();
    private static final Map<Integer, Epic> epic = new HashMap<>();
    private static final Map<Integer, SubTask> subTask = new HashMap<>();
    private final Task nullTask = new Task("Ошибка", "Такого ID не существует", Status.ERROR);

    int getNewID() {
        return ++taskID;
    }

    public int createTask(Task newTask) {
        task.put(getNewID(), newTask);
        newTask.setId(taskID);
        return newTask.getId();
    }

    public int createEpic(Epic newEpic) {
        epic.put(getNewID(), newEpic);
        newEpic.setId(taskID);
        newEpic.setStatus(Status.NEW);
        return newEpic.getId();
    }

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

    public void updateTask (int id, Task newTask) {
        if (task.containsKey(id)) {
            task.put(id, newTask);
            newTask.setId(id);
        } else {
            System.out.println("Нет задачи с таким ID");
            return;
        }
    }

    public void updateEpic(int id, Epic newEpic) {
        if (epic.containsKey(id)) {
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

    public void updateSubTask(int id, SubTask newSubTask) {
        if (subTask.containsKey(id))
            if (epic.containsKey(newSubTask.getEpicID())) {
                if (subTask.get(id).getEpicID() != newSubTask.getEpicID()) {
                    epic.get(subTask.get(id).getEpicID()).deleteSubTask(id);
                    epic.get(newSubTask.getEpicID()).addSubTasks(id);
                    updateEpicStatus(subTask.get(id).getEpicID());
                }
                subTask.put(id, newSubTask);
                newSubTask.setId(id);
                updateEpicStatus(newSubTask.getEpicID());
            } else {
                System.out.println("Нельзя добавить подзадачу для несуществующего эпика");
            }
         else {
            System.out.println("Нет подзадачи с таким ID");
            return;
        }
    }

    public ArrayList<Task> getTaskList() {
        ArrayList<Task> taskList = new ArrayList<>();
        taskList.addAll(task.values());
        return taskList;
    }

    public ArrayList<Epic> getEpicList() {
        ArrayList<Epic> epicList = new ArrayList<>();
        epicList.addAll(epic.values());
        return epicList;
    }

    public ArrayList<SubTask> getSubTaskList() {
        ArrayList<SubTask> subTaskList = new ArrayList<>();
        subTaskList.addAll(subTask.values());
        return subTaskList;
    }

    public void deleteAllTask() {
        task.clear();
    }

    public void deleteAllEpic() {
        epic.clear();
        subTask.clear();
    }

    public void deleteAllSubTask() {
        subTask.clear();
        for (Epic epicObject : epic.values()) {
            epicObject.setStatus(Status.NEW);
            epicObject.clearSubTasks();
        }
    }

    public void deleteByID(int id) {
        if (task.containsKey(id) || epic.containsKey(id) || subTask.containsKey(id)) {
            if (task.containsKey(id)) {
                task.remove(id);
            } else if (epic.containsKey(id)) {
                ArrayList<Integer> subTasksID = epic.get(id).getSubTasks();
                for (Integer subTaskID : subTasksID) {
                    subTask.remove(subTaskID);
                }
                epic.remove(id);
            } else if (subTask.containsKey(id)) {
                epic.get(subTask.get(id).getEpicID()).deleteSubTask(id);
                updateEpicStatus(subTask.get(id).getEpicID());
                subTask.remove(id);
            }
        } else {
            System.out.println("Ошибка! Такого ID не существует");
            return;
        }
    }

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
            return container;
        } else {
            System.out.println("Ошибка! Такого ID не существует");
            return nullTask;
        }
    }

    public ArrayList<SubTask> getEpicSubTasks (int epicID) {
        if (epic.containsKey(epicID)) {
            ArrayList<Integer> subTasksID = epic.get(epicID).getSubTasks();
            ArrayList<SubTask> epicSubTasks = new ArrayList<>();
            for (Integer subTaskID : subTasksID) {
                epicSubTasks.add(subTask.get(subTaskID));
            }
            return epicSubTasks;
        } else {
            System.out.println("Ошибка! Эпика с таким ID не существует");
            ArrayList<SubTask> epicSubTasks = new ArrayList<>();
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
