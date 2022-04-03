package сontroller;

import data.*;
import exception.ManagerSaveException;

import java.io.*;
import java.util.List;

public class FileBackedTasksManager extends InMemoryTaskManager{
    private File taskData;
    public  FileBackedTasksManager(File taskData) {
        this.taskData = taskData;
    }

    private void save() {
        try (Writer fileWriter = new FileWriter(taskData)) {
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

    public void loadFromFile() {
        try (BufferedReader fileReader = new BufferedReader(new FileReader(taskData))) {
            String line;
            boolean isHistoryString = false;

            while (fileReader.ready()) {
                line = fileReader.readLine();
                if (line.equals("")) {
                    isHistoryString = true;
                    continue;
                }
                if (isHistoryString == true) {
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

    private void taskFromString(String value) {
        String[] splitValue = value.split(",");
        if (splitValue[1].equals(String.valueOf(TaskType.TASK))) {
            super.putTask(new Task(Long.parseLong(splitValue[0]), splitValue[2], splitValue[4],
                    Status.valueOf(splitValue[3])));
        } else if (splitValue[1].equals(String.valueOf(TaskType.EPIC))) {
            super.putEpic(new Epic(Long.parseLong(splitValue[0]), splitValue[2], splitValue[4],
                    Status.valueOf(splitValue[3])));
        } else if (splitValue[1].equals(String.valueOf(TaskType.SUBTASK))) {
            super.putSubTask(new SubTask(Long.parseLong(splitValue[0]), splitValue[2], splitValue[4],
                    Status.valueOf(splitValue[3]), Long.parseLong(splitValue[5])));
            super.epic.get(Long.parseLong(splitValue[5])).addSubTask(Long.parseLong(splitValue[0]));
        }
        super.setTaskID(Long.max(Long.parseLong(splitValue[0]), super.getTaskID()));
    }

    private void historyFromString(String history) {
        String[] splitHistory = history.split(",");
        for (String id : splitHistory) {
            if (task.containsKey(Long.parseLong(id))) {
                Managers.getDefaultHistoryManager().add(super.task.get(Long.parseLong(id)));
            }
            if (epic.containsKey(Long.parseLong(id))) {
                Managers.getDefaultHistoryManager().add(super.epic.get(Long.parseLong(id)));
            }
            if (subTask.containsKey(Long.parseLong(id))) {
                Managers.getDefaultHistoryManager().add(super.subTask.get(Long.parseLong(id)));
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
}
