package tests;

import data.Status;
import data.SubTask;
import org.junit.jupiter.api.Test;
import сontroller.FileBackedTasksManager;
import сontroller.Managers;
import сontroller.TaskManager;

import java.io.File;
import static org.junit.jupiter.api.Assertions.*;

class FileBackedTasksManagerTest extends TaskManagerTest<FileBackedTasksManager> {
    File file = new File("src/data/TaskData.csv");
    @Override
    void setTaskManager () {
        taskManager = Managers.getDefaultFileBackedTasksManager();
    }

    @Test
    public void saveLoadTest() {
        taskManager.deleteAllEpic();
        taskManager.deleteAllTask();
        TaskManager secondTaskManager = Managers.getDefaultTaskManager();
        FileBackedTasksManager.loadFromFile(file);
        assertEquals(0, secondTaskManager.getEpicList().size(), "Эпики загрузились с ошибкой");
        assertEquals(0, secondTaskManager.getTaskList().size(), "Задачи загрузились с ошибкой");
        assertEquals(0, secondTaskManager.getSubTaskList().size(), "Подзадачи загрузились с ошибкой");

        long epic2Id = taskManager.createEpic(epic2);
        FileBackedTasksManager.loadFromFile(file);
        assertEquals(epic2, secondTaskManager.getByID(epic2Id), "Пустой эпик загрузился с ошибкой");
        taskManager.deleteAllEpic();

        long epic1Id = taskManager.createEpic(epic1);
        epic2Id = taskManager.createEpic(epic2);
        long subTask1Id = taskManager.createSubTask(new SubTask("Подзадача 1 Эпика 1",
                "Описание", Status.NEW, epic1Id));
        long subTask2Id = taskManager.createSubTask(new SubTask("Подзадача 2 Эпика 1",
                "Описание", Status.DONE, epic1Id));
        long task1Id = taskManager.createTask(task1);
        long task2Id = taskManager.createTask(task2);
        FileBackedTasksManager.loadFromFile(file);
        assertEquals(2, secondTaskManager.getEpicList().size(), "Эпики загрузились с ошибкой");
        assertEquals(2, secondTaskManager.getTaskList().size(), "Задачи загрузились с ошибкой");
        assertEquals(2, secondTaskManager.getSubTaskList().size(), "Подзадачи загрузились с ошибкой");
        assertEquals(0, secondTaskManager.history().size(), "Пустая история загрузилась с ошибкой");

        taskManager.getByID(epic1Id);
        taskManager.getByID(task1Id);
        taskManager.getByID(subTask1Id);
        taskManager.getByID(epic2Id);
        taskManager.getByID(subTask2Id);
        taskManager.getByID(task2Id);
        FileBackedTasksManager.loadFromFile(file);
        assertEquals(6, secondTaskManager.history().size(), "История загрузилась с ошибкой");
        assertEquals(epic1, secondTaskManager.history().get(0), "Последовательность истории загрузилась с ошибкой");
        assertEquals(epic2, secondTaskManager.history().get(3), "Последовательность истории загрузилась с ошибкой");
        assertEquals(task2, secondTaskManager.history().get(5), "Последовательность истории загрузилась с ошибкой");
    }
}