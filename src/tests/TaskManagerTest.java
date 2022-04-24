package tests;
import data.Epic;
import data.Status;
import data.SubTask;
import data.Task;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import сontroller.TaskManager;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

abstract class TaskManagerTest <T extends TaskManager> {
    T taskManager;
    long epicId;
    Epic epic1;
    Epic epic2;
    SubTask subTask1;
    SubTask subTask2;
    Task task1;
    Task task2;
    Duration duration = Duration.ofHours(3);

    abstract void setTaskManager();

    @BeforeEach
    public void beforeEach() {
        setTaskManager();
        epic1 = new Epic("Эпик 1", "Описание 1", Status.DONE);
        epic2 = new Epic("Эпик 2", "Описание 2", Status.IN_PROGRESS);
        epicId = taskManager.createEpic(epic1);
        subTask1 = new SubTask("Подзадача 1 Эпика 1","Описание", Status.NEW,
                LocalDateTime.of(2022, 4,23, 12, 0), duration, epicId);
        subTask2 = new SubTask("Подзадача 2 Эпика 1","Описание", Status.DONE,
                LocalDateTime.of(2022, 4,24, 15, 0), duration, epicId);
        task1 = new Task("Задача 1", "Описание", Status.IN_PROGRESS,
                LocalDateTime.of(2022, 4,24, 12, 0), duration);
        task2 = new Task("Задача 2", "Описание", Status.DONE);
    }

    @AfterEach
    public void afterEach() {
        taskManager.deleteAllEpic();
        taskManager.deleteAllTask();
        taskManager.deleteAllSubTask();
    }

    @Test
    public void createTaskTest() {
        final long taskId = taskManager.createTask(task1);
        final Task savedTask = taskManager.getByID(taskId);

        assertNotNull(savedTask, "Задача не найдена");
        assertEquals(task1, savedTask, "Задачи не совпадают");

        final List<Task> tasks = taskManager.getTaskList();

        assertNotNull(tasks, "Задачи не возвращаются.");
        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(task1, tasks.get(0), "Задачи не совпадают.");
    }

    @Test
    public void createEpicTest() {
        final Task savedEpic = taskManager.getByID(epicId);

        assertNotNull(savedEpic, "Эпик не найден");
        assertEquals(epic1, savedEpic, "Эпики не совпадают");

        final List<Epic> epics = taskManager.getEpicList();

        assertNotNull(epics, "Эпики не возвращаются.");
        assertEquals(1, epics.size(), "Неверное количество эпиков.");
        assertEquals(epic1, epics.get(0), "Эпики не совпадают.");
    }

    @Test
    public void createSubTaskTest() {
        final long subTaskId = taskManager.createSubTask(subTask1);
        final Task savedSubTask = taskManager.getByID(subTaskId);

        assertNotNull(savedSubTask, "Подзадача не найдена");
        assertEquals(subTask1, savedSubTask, "Подзадачи не совпадают");

        final List<SubTask> subTasks = taskManager.getSubTaskList();

        assertNotNull(subTasks, "Подзадача не возвращается.");
        assertEquals(1, subTasks.size(), "Неверное количество подзадач.");
        assertEquals(subTask1, subTasks.get(0), "Подзадачи не совпадают.");
    }

    @Test
    public void updateTask() {
        final long taskId = taskManager.createTask(task1);
        final IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> taskManager.updateTask(100, task2));
        assertEquals("Нет задачи с таким ID", exception.getMessage());

        final IllegalArgumentException exception2 = assertThrows(IllegalArgumentException.class,
                () -> taskManager.updateTask(-1, task2));
        assertEquals("Нет задачи с таким ID", exception2.getMessage());

        taskManager.updateTask(taskId, task2);
        assertEquals(task2, taskManager.getByID(taskId), "Задача не обновилась");
    }

    @Test
    public void updateEpicTest() {
        final IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> taskManager.updateEpic(100, epic2));
        assertEquals("Нет эпика с таким ID", exception.getMessage());

        final IllegalArgumentException exception2 = assertThrows(IllegalArgumentException.class,
                () -> taskManager.updateEpic(-1, epic2));
        assertEquals("Нет эпика с таким ID", exception2.getMessage());

        taskManager.updateEpic(epicId, epic2);
        assertEquals(epic2, taskManager.getByID(epicId), "Эпик не обновился");
    }

    @Test
    public void updateSubTaskTest() {
        long subTask1Id = taskManager.createSubTask(subTask1);
        final IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> taskManager.updateSubTask(100, subTask2));
        assertEquals("Нет подзадачи с таким ID", exception.getMessage());

        final IllegalArgumentException exception2 = assertThrows(IllegalArgumentException.class,
                () -> taskManager.updateSubTask(-1, subTask2));
        assertEquals("Нет подзадачи с таким ID", exception2.getMessage());

        final IllegalArgumentException exception3 = assertThrows(IllegalArgumentException.class,
                () -> taskManager.updateSubTask(subTask1Id, new SubTask("Подзадача для несуществующего эпика",
                        "Описание", Status.DONE, -1)));
        assertEquals("Нельзя добавить подзадачу для несуществующего эпика", exception3.getMessage());

        taskManager.updateSubTask(subTask1Id, subTask2);
        assertEquals(subTask2, taskManager.getByID(subTask1Id));
    }

    @Test
    public void getTaskListTest() {
       assertEquals(0, taskManager.getTaskList().size(), "Некорректная работа при пустом списке задач");
       taskManager.createTask(task1);
       taskManager.createTask(task2);
       assertEquals(2, taskManager.getTaskList().size(),
               "Неправильная длина возвращаемого списка задач");
    }

    @Test
    public void getEpicListTest() {
        taskManager.deleteAllEpic();
        assertEquals(0, taskManager.getEpicList().size(), "Некорректная работа при пустом списке");
        taskManager.createEpic(epic1);
        taskManager.createEpic(epic2);
        assertEquals(2, taskManager.getEpicList().size(),
                "Неправильная длина возвращаемого списка");
    }

    @Test
    public void getSubTaskListTest() {
        assertEquals(0, taskManager.getSubTaskList().size(), "Некорректная работа при пустом списке");
        taskManager.createSubTask(subTask1);
        taskManager.createSubTask(subTask2);
        assertEquals(2, taskManager.getSubTaskList().size(),
                "Неправильная длина возвращаемого списка");
    }

    @Test
    public void deleteAllTaskTest() {
        taskManager.createTask(task1);
        taskManager.createTask(task2);
        assertEquals(2, taskManager.getTaskList().size(), "Неверное количество задач.");
        taskManager.deleteAllTask();
        assertEquals(0, taskManager.getTaskList().size(), "Задачи не удалены");
    }

    @Test
    public void deleteAllEpicTest() {
        taskManager.createEpic(epic2);
        taskManager.createSubTask(subTask1);
        taskManager.createSubTask(subTask2);

        assertEquals(2, taskManager.getEpicList().size(), "Неверное количество эпиков.");
        assertEquals(2, taskManager.getSubTaskList().size(), "Неверное количество подзадач.");
        taskManager.deleteAllEpic();
        assertEquals(0, taskManager.getEpicList().size(), "Эпики не удалены");
        assertEquals(0, taskManager.getSubTaskList().size(), "Подзадачи не удалены");
    }

    @Test
    public void deleteAllSubTaskTest() {
        taskManager.createSubTask(subTask1);
        taskManager.createSubTask(subTask2);

        assertEquals(Status.IN_PROGRESS, epic1.getStatus(), "Статус эпика не обновляется");
        assertEquals(1, taskManager.getEpicList().size(), "Неверное количество эпиков.");
        assertEquals(2, taskManager.getSubTaskList().size(), "Неверное количество подзадач.");

        taskManager.deleteAllSubTask();
        assertEquals(Status.NEW, epic1.getStatus(), "Статус эпика не обновляется");
        assertEquals(0, taskManager.getSubTaskList().size(), "Подзадачи не удалены");
    }

    @Test
    public void deleteByIDTest() {
        final IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> taskManager.deleteByID(-1));
        assertEquals("Такого ID не существует", exception.getMessage());

        final IllegalArgumentException exception2 = assertThrows(IllegalArgumentException.class,
                () -> taskManager.deleteByID(10));
        assertEquals("Такого ID не существует", exception2.getMessage());


        taskManager.createSubTask(subTask1);
        long taskId = taskManager.createTask(task1);

        taskManager.deleteByID(taskId);
        assertEquals(0, taskManager.getTaskList().size(), "Задача не удалена");

        taskManager.deleteByID(epicId);
        assertEquals(0, taskManager.getEpicList().size(), "Эпик не удален");
        assertEquals(0, taskManager.getSubTaskList().size(), "Подзадачи эпика не удалены");
    }

    @Test
    public void getByIDTest() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> taskManager.getByID(-1));
        assertEquals("Такого ID не существует", exception.getMessage(),
                "Неправильная работа при значении id -1");

        IllegalArgumentException exception1 = assertThrows(IllegalArgumentException.class,
                () -> taskManager.getByID(100));
        assertEquals("Такого ID не существует", exception1.getMessage(),
                "Неправильная работа при значении id 100");

        assertEquals(epic1, taskManager.getByID(epicId), "Эпик не возвращается по id");
    }

    @Test
    public void getEpicSubTasksTest() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> taskManager.getEpicSubTasks(-1));
        assertEquals("Эпика с таким ID не существует", exception.getMessage(),
                "Неправильная работа при несуществующем эпике");

        assertEquals(0, taskManager.getEpicSubTasks(epicId).size(),
                "Неправильная работа при отсутствии в эпике подзадач");

        taskManager.createSubTask(subTask1);
        taskManager.createSubTask(subTask2);

        final List<SubTask> subTasks = taskManager.getEpicSubTasks(epicId);
        assertEquals(subTask1, subTasks.get(0), "Подзадача 1 не совпадает");
        assertEquals(subTask2, subTasks.get(1), "Подзадача 2 не совпадает");
        assertEquals(2, subTasks.size(), "Количество подзадач не совпадает");
    }

    @Test
    public void historyTest() {
        long task1Id = taskManager.createTask(task1);
        long subTask1Id = taskManager.createTask(subTask1);
        long subTask2Id= taskManager.createTask(subTask2);
        taskManager.getByID(epicId);
        taskManager.getByID(task1Id);
        taskManager.getByID(subTask1Id);
        taskManager.getByID(subTask2Id);

        assertEquals(4, taskManager.history().size(), "Размер списка (Истории) не совпадает");
        assertEquals(epic1, taskManager.history().get(0));
        assertEquals(task1, taskManager.history().get(1));
        assertEquals(subTask2, taskManager.history().get(3));
        taskManager.getByID(epicId);
        assertEquals(epic1, taskManager.history().get(3));
    }

    @Test
    public void updateEpicStatusTest() {
        assertEquals(Status.NEW, taskManager.getByID(epicId).getStatus(),
                "Статус не равен NEW при пустом списке подзадач");

        long subTask1Id = taskManager.createSubTask(new SubTask("Подзадача 1 Эпика 1",
                "Описание", Status.NEW, epicId));
        long subTask2Id = taskManager.createSubTask(new SubTask("Подзадача 2 Эпика 1",
                "Описание", Status.NEW, epicId));
        assertEquals(Status.NEW, taskManager.getByID(epicId).getStatus(),
                "Статус не равен NEW при подзадачах со статусом NEW");

        taskManager.updateSubTask(subTask1Id, new SubTask("Подзадача 1 Эпика 1",
                "Описание", Status.DONE, epicId));
        taskManager.updateSubTask(subTask2Id, new SubTask("Подзадача 2 Эпика 1",
                "Описание", Status.DONE, epicId));
        assertEquals(Status.DONE, taskManager.getByID(epicId).getStatus(),
                "Статус не равен DONE при подзадачах со статусом DONE");

        taskManager.updateSubTask(subTask1Id, new SubTask("Подзадача 1 Эпика 1",
                "Описание", Status.NEW, epicId));
        taskManager.updateSubTask(subTask2Id, new SubTask("Подзадача 2 Эпика 1",
                "Описание", Status.DONE, epicId));
        assertEquals(Status.IN_PROGRESS, taskManager.getByID(epicId).getStatus(),
                "Статус не равен IN_PROGRESS при подзадачах со статусом NEW и DONE");

        taskManager.updateSubTask(subTask1Id, new SubTask("Подзадача 1 Эпика 1",
                "Описание", Status.IN_PROGRESS, epicId));
        taskManager.updateSubTask(subTask2Id, new SubTask("Подзадача 2 Эпика 1",
                "Описание", Status.IN_PROGRESS, epicId));
        assertEquals(Status.IN_PROGRESS, taskManager.getByID(epicId).getStatus(),
                "Статус не равен IN_PROGRESS при подзадачах со статусом IN_PROGRESS");
    }

    @Test
    public void getPrioritizedTasksTest() {
        long task1Id = taskManager.createTask(new Task("Задача 1", "Описание", Status.IN_PROGRESS,
                LocalDateTime.of(2022, 4,21, 12, 0), duration));
        long task2Id = taskManager.createTask(new Task("Задача 2", "Описание", Status.DONE));
        long subTask1Id = taskManager.createSubTask(new SubTask("Подзадача 1 Эпика 1","Описание",
                Status.NEW, LocalDateTime.of(2022, 4,24, 12, 0), duration, epicId));
        long subTask2Id = taskManager.createSubTask(new SubTask("Подзадача 1 Эпика 1","Описание",
                Status.NEW, LocalDateTime.of(2022, 4,24, 15, 0), duration, epicId));

        assertEquals(4 , taskManager.getPrioritizedTasks().size(),
                "Не все задачи добавлены в список приоритетных задач");
        assertEquals(taskManager.getByID(task1Id), taskManager.getPrioritizedTasks().get(0),
                "Приоритет задач рассчитывается с ошибкой");
        assertEquals(taskManager.getByID(subTask1Id), taskManager.getPrioritizedTasks().get(1),
                "Приоритет задач рассчитывается с ошибкой," +
                        " при совпадении даты старта новой задачи с датой окончания предыдущей");
        assertEquals(taskManager.getByID(subTask2Id), taskManager.getPrioritizedTasks().get(2),
                "Приоритет задач рассчитывается с ошибкой," +
                        " при совпадении даты старта новой задачи с датой окончания предыдущей");
        assertEquals(taskManager.getByID(task2Id), taskManager.getPrioritizedTasks().get(3),
                "Приоритет задач рассчитывается с ошибкой, пустая задача не перемещается в конец списка");


        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> taskManager.updateTask(task1Id, new Task("Задача 1", "Описание", Status.IN_PROGRESS,
                        LocalDateTime.of(2022, 4,24, 12, 0), duration)));
        assertEquals("Задача не может начинаться во время выполнения другой задачи", exception.getMessage(),
                "Неправильная работа при пересечении даты старта задачи");

        exception = assertThrows(IllegalArgumentException.class,
                () -> taskManager.updateTask(task1Id, new Task("Задача 1", "Описание", Status.IN_PROGRESS,
                        LocalDateTime.of(2022, 4,24, 11, 0), duration)));
        assertEquals("Задача не может завершаться позже начала новой задачи", exception.getMessage(),
                "Неправильная работа при пересечении даты окончания задачи с датой старта новой");


        exception = assertThrows(IllegalArgumentException.class,
                () -> taskManager.updateTask(task1Id, new Task("Задача 1", "Описание", Status.IN_PROGRESS,
                        LocalDateTime.of(2022, 4,24, 11, 0), Duration.ofHours(4))));
        assertEquals("Задача не может завершаться позже начала новой задачи", exception.getMessage(),
                "Неправильная работа при пересечении даты окончания задачи с датой окончания новой задачи");
    }
}
