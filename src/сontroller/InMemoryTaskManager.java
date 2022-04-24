package сontroller;

import data.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

public class InMemoryTaskManager implements TaskManager {
    private static long taskID = 0;
    protected static final Map<Long, Task> task = new HashMap<>(); // в слаке началось обсуждение о том что эти Мапы
    // не должны быть статическими, насколько это корректно? Вместе с тем, если они не статик,
    // не очень понятно как работать с ними в методе loadFromFile
    protected static final Map<Long, Epic> epic = new HashMap<>();
    protected static final Map<Long, SubTask> subTask = new HashMap<>();
    protected final HistoryManager historyManager = new InMemoryHistoryManager();
    private final Task nullTask = new Task("Ошибка", "Такого ID не существует", Status.ERROR,
            LocalDateTime.now(),  Duration.between(LocalDateTime.now(), LocalDateTime.now()));
    private static final Comparator<Task> comparator = (o1, o2) -> {
            if (o1.getStartTime().isEmpty()) return 1;
            if (o2.getStartTime().isEmpty()) return -1;
            if (o1.getStartTime().get().isBefore(o2.getStartTime().get())) {
                return -1;
            } else if (o2.getStartTime().get().isBefore(o1.getStartTime().get())) {
                return 1;
            } else return 0;
    };
    private static TreeSet<Task> prioritizedTasks = new TreeSet<>(comparator);

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
        if (isValidStartEndTime(newTask)) {
            task.put(getNewID(), newTask);
            newTask.setId(taskID);
            updatePrioritizedTasks();
            return newTask.getId();
        } else
            throw new IllegalArgumentException("Не удалось добавить задачу");
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
            if (isValidStartEndTime(newSubTask)) {
                subTask.put(getNewID(), newSubTask);
                newSubTask.setId(taskID);
                epic.get(newSubTask.getEpicID()).addSubTask(taskID);
                updateEpicStatus(newSubTask.getEpicID());
                updateEpicStartAndEndTime(newSubTask.getEpicID());
                updatePrioritizedTasks();
                return newSubTask.getId();
            } else
                throw new IllegalArgumentException("Не удалось добавить подзадачу");
        } else {
            throw new IllegalArgumentException ("Нельзя добавить подзадачу для несуществующего эпика");
        }
    }

    @Override
    public void updateTask (long id, Task newTask) {
        if (task.containsKey(id)) {
            if (isValidStartEndTime(newTask)) {
                newTask.setId(id);
                updatePrioritizedTasks();
                task.put(id, newTask);
                InMemoryHistoryManager.update(id, newTask);
            } else
                throw new IllegalArgumentException("Не удалось обновить задачу");
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
                if (isValidStartEndTime(newSubTask)) {
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
                    updatePrioritizedTasks();
                } else
                    throw new IllegalArgumentException("Не удалось обновить подзадачу");
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
        updatePrioritizedTasks();
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
        updatePrioritizedTasks();
        subTask.clear();
    }

    @Override
    public void deleteAllSubTask() {
        for (SubTask subTaskToRemove : subTask.values()) {
            historyManager.remove(subTaskToRemove);
        }
        updatePrioritizedTasks();
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
                prioritizedTasks.remove(task.get(id));
                task.remove(id);
            } else if (epic.containsKey(id)) {
                ArrayList<Long> subTasksID = epic.get(id).getSubTasks();
                for (Long subTaskID : subTasksID) {
                    historyManager.remove(subTask.get(subTaskID));
                    prioritizedTasks.remove(subTask.get(subTaskID));
                    subTask.remove(subTaskID);
                }
                historyManager.remove(epic.get(id));
                epic.remove(id);
            } else if (subTask.containsKey(id)) {
                epic.get(subTask.get(id).getEpicID()).deleteSubTask(id);
                updateEpicStatus(subTask.get(id).getEpicID());
                historyManager.remove(subTask.get(id));
                prioritizedTasks.remove(subTask.get(id));
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
            if (subTask.get(subTaskID).getStartTime().isPresent()) {
                if (startTime.isAfter(subTask.get(subTaskID).getStartTime().get())) {
                    startTime = subTask.get(subTaskID).getStartTime().get();
                }
                if (endTime.isBefore(subTask.get(subTaskID).getEndTime().get())) {
                    endTime = subTask.get(subTaskID).getEndTime().get();
                }
                epicDuration.plus(subTask.get(subTaskID).getDuration());
            }
        }
        if (startTime != LocalDateTime.MIN) {
            epic.get(epicID).setDuration(epicDuration);
            epic.get(epicID).setStartTime(startTime);
            epic.get(epicID).setEndTime(endTime);
        }
    }

    @Override
    public List<Task> history() {
        return historyManager.getHistory();
    }

    protected static void updatePrioritizedTasks() { // появилось ощущение, что пишу ****код (хотя вроде все работает),
        // критика приветствуется=)
        prioritizedTasks.clear();
        if (task.size() != 0)
        prioritizedTasks.addAll(task.values());
        if (subTask.size() != 0)
        prioritizedTasks.addAll(subTask.values());
    }

    private boolean isValidStartEndTime(Task task) {
        if (task.getStartTime().isEmpty())
            return true;
        List<Task> sortedTasks = getPrioritizedTasks();
        boolean isValidStartTime = true;
        boolean isValidEndTime = true;
        for (Task sortedTask : sortedTasks) {
            if (sortedTask.getStartTime().isPresent()) {
                if (task.getStartTime().get().isAfter(sortedTask.getStartTime().get()) &&
                        task.getStartTime().get().isBefore(sortedTask.getEndTime().get()) ||
                        task.getStartTime().equals(sortedTask.getStartTime())) {
                    isValidStartTime = false;
                }
                if (task.getEndTime().get().isAfter(sortedTask.getStartTime().get()) &&
                        task.getEndTime().get().isBefore(sortedTask.getEndTime().get()) ||
                        task.getEndTime().equals(sortedTask.getEndTime())) {
                    isValidEndTime = false;
                }
            }
        }
        if (isValidStartTime && isValidEndTime)
           return true;
        else if (!isValidStartTime)
            throw new IllegalArgumentException("Задача не может начинаться во время выполнения другой задачи");
        else
            throw new IllegalArgumentException("Задача не может завершаться позже начала новой задачи");
    }

    @Override
    public List<Task> getPrioritizedTasks() { // Исходя из ТЗ эпиков в этом списке нет
        return new ArrayList<>(prioritizedTasks);
    }
}
