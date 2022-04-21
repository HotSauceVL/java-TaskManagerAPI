package tests;

import data.Epic;
import data.Status;
import data.SubTask;
import data.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import сontroller.HistoryManager;
import сontroller.InMemoryHistoryManager;
import сontroller.Managers;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class HistoryManagerTest {
    HistoryManager historyManager;
    Epic epic;
    Task task;
    SubTask subTask;

    @BeforeEach
    public void beforeEach() {
        historyManager = Managers.getDefaultHistoryManager();
        epic = new Epic(1,"Эпик 1", "Описание 1", Status.NEW);
        task = new Task(2,"Задача 1", "Описание",Status.IN_PROGRESS);
        subTask = new SubTask(3,"Подзадача 1 Эпика 1","Описание",Status.NEW, 1);
        historyManager.remove(epic);
        historyManager.remove(task);
        historyManager.remove(subTask);
    }

    @Test
    void addTest() {
        historyManager.add(task);
        historyManager.add(epic);
        historyManager.add(subTask);
        final List<Task> history = historyManager.getHistory();
        assertNotNull(history, "История не пустая.");
        assertEquals(3, history.size(), "История не пустая.");
    }

    @Test
    void getHistoryTest() {
        assertEquals(0,historyManager.getHistory().size(), "История пустая");

        historyManager.add(task);
        historyManager.add(epic);
        historyManager.add(subTask);
        List<Task> history = historyManager.getHistory();

        assertNotNull(history, "История не пустая.");
        assertEquals(3, history.size(), "История не пустая.");
        assertEquals(task, history.get(0), "Задача не сохранилась в историю");
        assertEquals(epic, history.get(1), "Эпик не сохранился в историю");
        assertEquals(subTask, history.get(2), "Подазадача не сохранилась в историю");

        historyManager.add(task);
        history = historyManager.getHistory();
        assertEquals(task, history.get(2), "Задача не сохранилась в историю");
    }

    @Test
    void removeTest() {
        historyManager.add(task);
        historyManager.add(epic);
        historyManager.add(subTask);

        historyManager.remove(task);
        assertEquals(2, historyManager.getHistory().size(), "Удаление не сработало");
        assertEquals(epic, historyManager.getHistory().get(0), "Задача не удалилась из начала истории");

        historyManager.add(task);
        historyManager.remove(subTask);
        assertEquals(2, historyManager.getHistory().size(), "Удаление не сработало");
        assertEquals(task, historyManager.getHistory().get(1), "Подзадача не удалилась из середины истории");

        historyManager.remove(task);
        assertEquals(1, historyManager.getHistory().size(), "Удаление не сработало");
        assertFalse(historyManager.getHistory().contains(task), "Задача не удалилась из конца истории");
    }
}