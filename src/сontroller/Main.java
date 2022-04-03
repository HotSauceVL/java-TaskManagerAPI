package сontroller;

import data.*;
import data.Status;

import java.io.File;

public class Main {
    public static void main(String[] args) {

        File file = new File("src/data/TaskData.csv");
        FileBackedTasksManager taskManager = Managers.getDefaultFileBackedTasksManager();
        FileBackedTasksManager secondTaskManager =
                new FileBackedTasksManager(file);



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

        secondTaskManager.loadFromFile();
        System.out.println("История" + secondTaskManager.history());
        System.out.println(secondTaskManager.getEpicList());
        System.out.println(secondTaskManager.getTaskList());
        System.out.println(secondTaskManager.getSubTaskList());
    }
}
