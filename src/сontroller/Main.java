package сontroller;

import tasks.*;
import tasks.Status;

public class Main {
    public static void main(String[] args) {
        Managers managers = new Managers();

        TaskManager taskManager = managers.getDefault();
        HistoryManager historyManager = Managers.getDefaultHistory();

        int firstEpic = taskManager.createEpic(new Epic("Эпик 1", "Описание 1", Status.NEW));
        int firstSubTask = taskManager.createSubTask(new SubTask("Подзадача 1",
               "Описание", Status.NEW, firstEpic));
        int secondSubTask = taskManager.createSubTask(new SubTask("Подзадача 2",
               "Описание", Status.DONE, firstEpic));

        int secondEpic = taskManager.createEpic(new Epic("Эпик 2", "Описание 2", Status.IN_PROGRESS));
        int firstSubTaskSecondEpic = taskManager.createSubTask(new SubTask("Подзадача 1 второго эпика",
                "Описание", Status.DONE, secondEpic));

        int firstTask = taskManager.createTask(new Task("Задача 1", "Описание", Status.IN_PROGRESS));
        int secondTask = taskManager.createTask(new Task("Задача 2", "Описание", Status.DONE));

        taskManager.getByID(firstEpic);
        taskManager.getByID(firstEpic);
        taskManager.getByID(firstSubTask);
        taskManager.getByID(firstTask);
        taskManager.getByID(secondTask);


        System.out.println("История" + historyManager.getHistory());
        System.out.println(taskManager.getEpicList());
        System.out.println(taskManager.getSubTaskList());
        System.out.println(taskManager.getTaskList());

        taskManager.updateEpic(firstEpic, new Epic("Эпик 1", "Описание 1", Status.DONE));
        taskManager.updateSubTask(secondSubTask, new SubTask("Подзадача 1",
                "Описание", Status.DONE, secondEpic));
        taskManager.updateTask(secondTask, new Task("Задача 2", "Описание", Status.NEW));

        taskManager.getByID(firstEpic);
        taskManager.getByID(firstEpic);
        taskManager.getByID(firstSubTask);
        taskManager.getByID(firstTask);
        taskManager.getByID(secondTask);

        System.out.println(taskManager.getEpicList());
        System.out.println(taskManager.getSubTaskList());
        System.out.println(taskManager.getTaskList());
        System.out.println("История" + historyManager.getHistory());

        //taskManager.deleteByID(firstEpic);
        //taskManager.deleteByID(firstTask);
        taskManager.deleteAllEpic();
        System.out.println(taskManager.getEpicList());
        System.out.println("История" + historyManager.getHistory());

    }
}
