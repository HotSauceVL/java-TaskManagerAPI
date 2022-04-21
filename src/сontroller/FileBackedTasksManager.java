package сontroller;

import data.*;
import exception.ManagerSaveException;

import java.io.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

public class FileBackedTasksManager extends InMemoryTaskManager{
    private final File taskData;
    public  FileBackedTasksManager(File taskData) {
        this.taskData = taskData;
    }

    private void save() {
        try (Writer fileWriter = new FileWriter(taskData)) {
            fileWriter.write("id,type,name,status,description,epic,startTime,duration\n");
            if (super.getTaskList().size() != 0) {
                for (Task task : super.getTaskList()) {
                    fileWriter.write(task.taskToString());
                }
            }
            if (super.getEpicList().size() != 0) {
                for (Task epic : super.getEpicList()) {
                    fileWriter.write(epic.taskToString());
                }
            }
            if (super.getSubTaskList().size() != 0) {
                for (Task subTask : super.getSubTaskList()) {
                    fileWriter.write(subTask.taskToString());
                }
            }
            fileWriter.write("\n");
            fileWriter.write(historyToString(Managers.getDefaultHistoryManager()));
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка сохранения файла");
        }

    }

     public static void loadFromFile(File file) {
        try (BufferedReader fileReader = new BufferedReader(new FileReader(file))) {
            String line;
            boolean isHistoryString = false;

            while (fileReader.ready()) {
                line = fileReader.readLine();
                if (line.equals("id,type,name,status,description,epic,startTime,duration")) {
                    continue;
                }
                if (line.equals("")) {
                    isHistoryString = true;
                    continue;
                }
                if (isHistoryString) {
                    historyFromString(line);
                } else {
                    taskFromString(line);
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void taskFromString(String value) {
        String[] splitValue = value.split(",");
        if (splitValue[1].equals(String.valueOf(TaskType.TASK))) {
            if (splitValue.length == 5) {
                putTask(new Task(Long.parseLong(splitValue[0]), splitValue[2], splitValue[4],
                        Status.valueOf(splitValue[3])));
            } else {
                putTask(new Task(Long.parseLong(splitValue[0]), splitValue[2], splitValue[4],
                        Status.valueOf(splitValue[3]), LocalDateTime.parse(splitValue[5],
                        Task.getFormatter()), Duration.parse(splitValue[6])));
            }
        } else if (splitValue[1].equals(String.valueOf(TaskType.EPIC))) {
            putEpic(new Epic(Long.parseLong(splitValue[0]), splitValue[2], splitValue[4],
                    Status.valueOf(splitValue[3])));
        } else if (splitValue[1].equals(String.valueOf(TaskType.SUBTASK))) {
            if (splitValue.length == 6) {
                putSubTask(new SubTask(Long.parseLong(splitValue[0]), splitValue[2], splitValue[4],
                        Status.valueOf(splitValue[3]), Long.parseLong(splitValue[5])));
                epic.get(Long.parseLong(splitValue[5])).addSubTask(Long.parseLong(splitValue[0]));
            } else {
                putSubTask(new SubTask(Long.parseLong(splitValue[0]), splitValue[2], splitValue[4],
                        Status.valueOf(splitValue[3]), LocalDateTime.parse(splitValue[6],
                        Task.getFormatter()), Duration.parse(splitValue[7]), Long.parseLong(splitValue[5])));
                epic.get(Long.parseLong(splitValue[5])).addSubTask(Long.parseLong(splitValue[0]));
            }
        }
        setTaskID(Long.max(Long.parseLong(splitValue[0]), getTaskID()));
    }

    private static void historyFromString(String history) {
        String[] splitHistory = history.split(",");
        for (String id : splitHistory) {
            if (task.containsKey(Long.parseLong(id))) {
                Managers.getDefaultHistoryManager().add(task.get(Long.parseLong(id)));
            }
            if (epic.containsKey(Long.parseLong(id))) {
                Managers.getDefaultHistoryManager().add(epic.get(Long.parseLong(id)));
            }
            if (subTask.containsKey(Long.parseLong(id))) {
                Managers.getDefaultHistoryManager().add(subTask.get(Long.parseLong(id)));
            }
        }

    }

    public String historyToString(HistoryManager manager) {
        List<Task> taskList = manager.getHistory();
        String historyString = "";

        if (taskList.size() != 0) {
            for (Task task : taskList) {
                if (historyString.equals("")) {
                    historyString = String.valueOf(task.getId());
                } else {
                    historyString = historyString + "," + task.getId();
                }
            }
        }
        return historyString;
    }

    @Override
    public long createTask(Task newTask) {
        super.createTask(newTask);
        save();
        return newTask.getId();
    }

    @Override
    public long createEpic(Epic newEpic) {
        super.createEpic(newEpic);
        save();
        return newEpic.getId();
    }

    @Override
    public long createSubTask(SubTask newSubTask) {
        super.createSubTask(newSubTask);
        save();
        return newSubTask.getId();
    }

    @Override
    public void updateTask (long id, Task newTask) {
        super.updateTask(id, newTask);
        save();
    }

    @Override
    public void updateEpic (long id, Epic newEpic) {
        super.updateEpic(id, newEpic);
        save();
    }

    @Override
    public void updateSubTask (long id, SubTask newSubTask) {
        super.updateSubTask(id, newSubTask);
        save();
    }

    @Override
    public void deleteAllTask() {
        super.deleteAllTask();
        save();
    }

    @Override
    public void deleteAllEpic() {
        super.deleteAllEpic();
        save();
    }

    @Override
    public void deleteAllSubTask() {
        super.deleteAllSubTask();
        save();
    }

    @Override
    public void deleteByID(long id) {
        super.deleteByID(id);
        save();
    }

    @Override
    public Task getByID(long id) {
        Task task = super.getByID(id);
        save();
        return task;
    }

    static void main(String[] args) {
        File file = new File("src/data/TaskData.csv");
        FileBackedTasksManager taskManager = Managers.getDefaultFileBackedTasksManager();
        FileBackedTasksManager secondTaskManager =
                new FileBackedTasksManager(file);
        LocalDateTime start = LocalDateTime.of(2022, 04,17, 12, 0);
        LocalDateTime finish = LocalDateTime.of(2022, 04,20, 12, 0);

        long firstEpic = taskManager.createEpic(new Epic("Эпик 1", "Описание 1", Status.NEW));
        long firstSubTask = taskManager.createSubTask(new SubTask("Подзадача 1 Эпика 1",
                "Описание", Status.NEW, start, Duration.between(start, finish), firstEpic));
        long secondSubTask = taskManager.createSubTask(new SubTask("Подзадача 2 Эпика 1",
                "Описание", Status.DONE, start, Duration.between(start, finish), firstEpic));
        long thirdSubTask = taskManager.createSubTask(new SubTask("Подзадача 3 Эпика 1",
                "Описание", Status.DONE, firstEpic));

        long secondEpic = taskManager.createEpic(new Epic("Эпик 2", "Описание 2", Status.IN_PROGRESS));

        long firstTask = taskManager.createTask(new Task("Задача 1", "Описание", Status.IN_PROGRESS, start, Duration.between(start, finish)));
        long secondTask = taskManager.createTask(new Task("Задача 2", "Описание", Status.DONE, start, Duration.between(start, finish)));

        taskManager.getByID(firstEpic);
        taskManager.getByID(firstEpic);
        taskManager.getByID(secondEpic);
        taskManager.getByID(thirdSubTask);
        taskManager.getByID(firstSubTask);
        taskManager.getByID(firstEpic);
        taskManager.getByID(firstTask);
        taskManager.getByID(secondSubTask);
        taskManager.getByID(secondTask);
        taskManager.getByID(firstEpic);
        System.out.println("История" + taskManager.history());
        System.out.println(taskManager.getEpicList());
        System.out.println(taskManager.getTaskList());
        System.out.println(taskManager.getSubTaskList());

        loadFromFile(file);

        System.out.println("История" + secondTaskManager.history());

        System.out.println(secondTaskManager.getEpicList());
        System.out.println(secondTaskManager.getTaskList());
        System.out.println(secondTaskManager.getSubTaskList());
    }
}
