package сontroller;

import data.*;
import data.Status;

public class Main {
    public static void main(String[] args) {
        Managers managers = new Managers();

        TaskManager taskManager = managers.getDefault();
        HistoryManager historyManager = Managers.getDefaultHistory();

        long firstEpic = taskManager.createEpic(new Epic("Эпик 1", "Описание 1", Status.NEW));
        long firstSubTask = taskManager.createSubTask(new SubTask("Подзадача 1 Эпика 1",
               "Описание", Status.NEW, firstEpic));
        long secondSubTask = taskManager.createSubTask(new SubTask("Подзадача 2 Эпика 1",
               "Описание", Status.DONE, firstEpic));
        long thirdSubTask = taskManager.createSubTask(new SubTask("Подзадача 3 Эпика 1",
                "Описание", Status.DONE, firstEpic));

        long secondEpic = taskManager.createEpic(new Epic("Эпик 2", "Описание 2", Status.IN_PROGRESS));

        long firstTask = taskManager.createTask(new Task("Задача 1", "Описание", Status.IN_PROGRESS));
        long secondTask = taskManager.createTask(new Task("Задача 2", "Описание", Status.DONE));

        taskManager.getByID(firstEpic);
        System.out.println("История" + historyManager.getHistory());
        taskManager.getByID(firstEpic);
        System.out.println("История" + historyManager.getHistory());
        taskManager.getByID(secondEpic);
        System.out.println("История" + historyManager.getHistory());
        taskManager.getByID(thirdSubTask);
        System.out.println("История" + historyManager.getHistory());
        taskManager.getByID(firstSubTask);
        System.out.println("История" + historyManager.getHistory());
        taskManager.getByID(firstEpic);
        System.out.println("История" + historyManager.getHistory());
        taskManager.getByID(firstTask);
        System.out.println("История" + historyManager.getHistory());
        taskManager.getByID(secondSubTask);
        System.out.println("История" + historyManager.getHistory());
        taskManager.getByID(secondTask);
        System.out.println("История" + historyManager.getHistory());
        taskManager.getByID(firstEpic);
        System.out.println("История" + historyManager.getHistory());

        taskManager.deleteByID(firstTask);
        System.out.println("История" + historyManager.getHistory());

        taskManager.deleteByID(firstEpic);
        System.out.println("История" + historyManager.getHistory());

    }
}
