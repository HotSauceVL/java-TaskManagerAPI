package Controller;

import Tasks.*;
import Tasks.Status;

public class Main {
    public static void main(String[] args) {
        Manager manager = new Manager();

       int firstEpic = manager.createEpic(new Epic("Эпик 1", "Описание 1", Status.NEW));
       int firstSubTask = manager.createSubTask(new SubTask("Подзадача 1",
               "Описание", Status.NEW, firstEpic));
       int secondSubTask = manager.createSubTask(new SubTask("Подзадача 2",
               "Описание", Status.DONE, firstEpic));

       int secondEpic = manager.createEpic(new Epic("Эпик 2", "Описание 2", Status.IN_PROGRESS));
       int firstSubTaskSecondEpic = manager.createSubTask(new SubTask("Подзадача 1 второго эпика",
                "Описание", Status.DONE, secondEpic));

       int firstTask = manager.createTask(new Task("Задача 1", "Описание", Status.IN_PROGRESS));
       int secondTask = manager.createTask(new Task("Задача 2", "Описание", Status.DONE));

        System.out.println(manager.getEpicList());
        System.out.println(manager.getSubTaskList());
        System.out.println(manager.getTaskList());

        manager.updateEpic(firstEpic, new Epic("Эпик 1", "Описание 1", Status.DONE));
        manager.updateSubTask(secondSubTask, new SubTask("Подзадача 1",
                "Описание", Status.DONE, secondEpic));
        manager.updateTask(secondTask, new Task("Задача 2", "Описание", Status.NEW));

        System.out.println(manager.getEpicList());
        System.out.println(manager.getSubTaskList());
        System.out.println(manager.getTaskList());

    }
}
