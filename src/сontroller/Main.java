package сontroller;

import data.Epic;
import data.Status;
import data.SubTask;
import data.Task;

import java.io.IOException;
import java.net.URI;
import java.time.Duration;
import java.time.LocalDateTime;

public class Main {
    public static void main(String[] args) throws IOException {
        try {
            new KVServer().start();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        TaskManager taskManager = Managers.getDefaultTaskManager();


        Duration duration = Duration.ofHours(3);

        long firstEpic = taskManager.createEpic(new Epic("Эпик 1", "Описание 1", Status.NEW));
        long firstSubTask = taskManager.createSubTask(new SubTask("Подзадача 1 Эпика 1",
                "Описание", Status.NEW,
                LocalDateTime.of(2022, 4,24, 12, 0), duration, firstEpic));
        long secondSubTask = taskManager.createSubTask(new SubTask("Подзадача 2 Эпика 1",
                "Описание", Status.DONE,
                LocalDateTime.of(2022, 4,16, 12, 0), duration, firstEpic));
        long thirdSubTask = taskManager.createSubTask(new SubTask("Подзадача 3 Эпика 1",
                "Описание", Status.DONE, firstEpic));

        long secondEpic = taskManager.createEpic(new Epic("Эпик 2", "Описание 2", Status.IN_PROGRESS));

        long firstTask = taskManager.createTask(new Task("Задача 1", "Описание", Status.IN_PROGRESS,
                LocalDateTime.of(2022, 4,24, 20, 0), duration));
        long secondTask = taskManager.createTask(new Task("Задача 2", "Описание", Status.DONE,
                LocalDateTime.of(2022, 4,24, 19, 0), Duration.ofHours(1)));

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
        System.out.println(taskManager.getPrioritizedTasks());
        HttpTaskServer httpTaskServer = new HttpTaskServer(taskManager);
        httpTaskServer.start();

        try {
            KVTaskClient kvTaskClient = new KVTaskClient(URI.create("http://localhost:8078/"));

            System.out.println("Задачи" + kvTaskClient.load("tasks"));
            System.out.println("Эпики" + kvTaskClient.load("epics"));
            System.out.println("Подзадачи" + kvTaskClient.load("subTasks"));
            System.out.println("История" + kvTaskClient.load("history"));
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

    }
}
