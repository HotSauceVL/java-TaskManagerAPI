package tests;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import data.Epic;
import data.Status;
import data.SubTask;
import data.Task;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import сontroller.*;
import сontroller.serializers.EpicSerializer;
import сontroller.serializers.SubTaskSerializer;
import сontroller.serializers.TaskSerializer;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class HTTPTaskServerTest {
    HttpTaskServer httpTaskServer;
    KVServer kvServer;
    HttpClient client = HttpClient.newHttpClient();
    private final Gson gson = new GsonBuilder()
            .registerTypeAdapter(Task.class, new TaskSerializer())
            .registerTypeAdapter(SubTask.class, new SubTaskSerializer())
            .registerTypeAdapter(Epic.class, new EpicSerializer())
            .create();
    Duration duration = Duration.ofHours(3);

    TaskManager manager;

    @BeforeEach
    public void beforeEach() throws IOException {
        kvServer = new KVServer();
        kvServer.start();
        httpTaskServer = new HttpTaskServer(Managers.getDefaultTaskManager());
        httpTaskServer.start();
        manager = Managers.getDefaultTaskManager();
        manager.deleteAllTask();
        manager.deleteAllEpic();
    }

    @AfterEach
    public void afterEach() {
        httpTaskServer.stop();
        kvServer.stop();
    }

    @Test
    public void getTasksFromAPI() throws IOException, InterruptedException {
        long task1Id = manager.createTask(new Task("Задача 1", "Описание", Status.IN_PROGRESS,
                LocalDateTime.of(2022, 4,24, 12, 0), duration));
        long task2Id = manager.createTask(new Task("Задача 2", "Описание", Status.DONE));
        URI url = URI.create("http://localhost:8080/tasks/task/");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        JsonElement jsonElement = JsonParser.parseString(response.body());
        List<Task> taskList = gson.fromJson(jsonElement.toString(), new TypeToken<List<Task>>(){}.getType());
        assertEquals(manager.getByID(task1Id), taskList.get(0), "Задачи не совпадают");
        assertEquals(manager.getByID(task2Id), taskList.get(1), "Задачи не совпадают");
    }

    @Test
    public void getEpicsFromAPI() throws IOException, InterruptedException {
        long epic1Id = manager.createEpic(new Epic("Эпик 1", "Описание 1", Status.DONE));
        long epic2Id = manager.createEpic(new Epic("Эпик 2", "Описание 2", Status.IN_PROGRESS));
        URI url = URI.create("http://localhost:8080/tasks/epic/");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        JsonElement jsonElement = JsonParser.parseString(response.body());
        List<Epic> epicList = gson.fromJson(jsonElement.toString(), new TypeToken<List<Epic>>(){}.getType());
        assertEquals(manager.getByID(epic1Id), epicList.get(0), "Эпики не совпадают");
        assertEquals(manager.getByID(epic2Id), epicList.get(1), "Эпики не совпадают");
    }

    @Test
    public void getSubTasksFromAPI() throws IOException, InterruptedException {
        long epicId = manager.createEpic(new Epic("Эпик 1", "Описание 1", Status.DONE));
        long subTask1Id = manager.createSubTask(new SubTask("Подзадача 1 Эпика 1","Описание", Status.NEW
                , LocalDateTime.of(2022, 4,23, 12, 0), duration, epicId));
        long subTask2Id = manager.createSubTask(new SubTask("Подзадача 2 Эпика 1","Описание", Status.DONE
                , epicId));
        URI url = URI.create("http://localhost:8080/tasks/subtask/");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        JsonElement jsonElement = JsonParser.parseString(response.body());
        List<SubTask> subTaskList = gson.fromJson(jsonElement.toString(), new TypeToken<List<SubTask>>(){}.getType());
        assertEquals(manager.getByID(subTask1Id), subTaskList.get(0), "Подзадачи не совпадают");
        assertEquals(manager.getByID(subTask2Id), subTaskList.get(1), "Подзадачи не совпадают");
    }

    @Test
    public void getTaskByIdFromAPI() throws IOException, InterruptedException {
        long epicId = manager.createEpic(new Epic("Эпик 1", "Описание 1", Status.DONE));
        long subTaskId = manager.createSubTask(new SubTask("Подзадача 1 Эпика 1","Описание", Status.NEW
                , LocalDateTime.of(2022, 4,23, 12, 0), duration, epicId));
        long taskId = manager.createTask(new Task("Задача 1", "Описание", Status.IN_PROGRESS,
                LocalDateTime.of(2022, 4,24, 12, 0), duration));
        URI url = URI.create("http://localhost:8080/tasks/task/?id=" + epicId);
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        JsonElement jsonElement = JsonParser.parseString(response.body());
        Epic epic = gson.fromJson(jsonElement, Epic.class);
        assertEquals(epicId, epic.getId(), "ID эпиков не совпадают");

        url = URI.create("http://localhost:8080/tasks/subtask/?id=" + subTaskId);
        request = HttpRequest.newBuilder().uri(url).GET().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        jsonElement = JsonParser.parseString(response.body());
        SubTask subTask = gson.fromJson(jsonElement, SubTask.class);
        assertEquals(manager.getByID(subTaskId), subTask, "Подзадачи не совпадают");

        url = URI.create("http://localhost:8080/tasks/task/?id=" + taskId);
        request = HttpRequest.newBuilder().uri(url).GET().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        jsonElement = JsonParser.parseString(response.body());
        Task task = gson.fromJson(jsonElement, Task.class);
        assertEquals(manager.getByID(taskId), task, "Подзадачи не совпадают");
    }

    @Test
    public void addTaskViaAPI() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/tasks/task/");
        Task task = new Task("Задача 1", "Описание", Status.IN_PROGRESS,
                LocalDateTime.of(2022, 4,24, 12, 0), duration);
        HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(gson.toJson(task));
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(body).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode(), "Сервер не добавил задачу");
        assertEquals(1, manager.getTaskList().size(), "Задача не добавилась");
        assertEquals(task.getTitle(), manager.getTaskList().get(0).getTitle(), "Задачи не совпадают");
    }

    @Test
    public void addEpicViaAPI() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/tasks/epic/");
        Epic epic = new Epic("Эпик 1", "Описание 1", Status.DONE);
        HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(gson.toJson(epic));
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(body).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode(), "Сервер не добавил эпик");
        assertEquals(1, manager.getEpicList().size(), "Эпик не добавился");
        assertEquals(epic.getTitle(), manager.getEpicList().get(0).getTitle(), "Эпики не совпадают");
    }

    @Test
    public void addSubTaskViaAPI() throws IOException, InterruptedException {
        long epicId = manager.createEpic(new Epic("Эпик 1", "Описание 1", Status.DONE));
        URI url = URI.create("http://localhost:8080/tasks/subtask/");
        SubTask subTask = new SubTask("Подзадача 1 Эпика 1","Описание", Status.NEW
                , LocalDateTime.of(2022, 4,23, 12, 0), duration, epicId);
        HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(gson.toJson(subTask));
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(body).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode(), "Сервер не добавил подзадачу");
        assertEquals(1, manager.getSubTaskList().size(), "Подзадача не добавилась");
        assertEquals(subTask.getTitle(), manager.getSubTaskList().get(0).getTitle(), "Подзадачи не совпадают");
    }

    @Test
    public void updateTaskViaAPI() throws IOException, InterruptedException {
        long taskId = manager.createTask(new Task("Задача 1", "Описание", Status.IN_PROGRESS,
                LocalDateTime.of(2022, 4,24, 12, 0), duration));
        URI url = URI.create("http://localhost:8080/tasks/task/?id=" + taskId);
        Task task = new Task("Обновленная Задача 1", "Описание", Status.IN_PROGRESS,
                LocalDateTime.of(2022, 5,24, 12, 0), duration);
        HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(gson.toJson(task));
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(body).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode(), "Сервер не обновил задачу");
        assertEquals(task.getTitle(), manager.getTaskList().get(0).getTitle(), "Задачи не совпадают");
    }

    @Test
    public void updateEpicViaAPI() throws IOException, InterruptedException {
        long epicId = manager.createEpic(new Epic("Эпик 1", "Описание 1", Status.DONE));
        URI url = URI.create("http://localhost:8080/tasks/epic/?id=" + epicId);
        Epic epic = new Epic("Обновленный Эпик 1", "Описание 1", Status.DONE);
        HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(gson.toJson(epic));
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(body).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode(), "Сервер не обновил эпик");
        assertEquals(epic.getTitle(), manager.getEpicList().get(0).getTitle(), "Эпики не совпадают");
    }

    @Test
    public void updateSubTaskViaAPI() throws IOException, InterruptedException {
        long epicId = manager.createEpic(new Epic("Эпик 1", "Описание 1", Status.DONE));
        long subTaskId = manager.createSubTask(new SubTask("Подзадача 1 Эпика 1","Описание", Status.NEW
                , LocalDateTime.of(2022, 4,23, 12, 0), duration, epicId));
        URI url = URI.create("http://localhost:8080/tasks/subtask/?id=" + subTaskId);
        SubTask subTask = new SubTask(" Обновленная Подзадача 1 Эпика 1","Описание", Status.NEW
                , LocalDateTime.of(2022, 5,23, 12, 0), duration, epicId);
        HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(gson.toJson(subTask));
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(body).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode(), "Сервер не обновил подзадачу");
        assertEquals(subTask.getTitle(), manager.getSubTaskList().get(0).getTitle(), "Подзадачи не совпадают");
    }

    @Test
    public void deleteAllTaskViaAPI() throws IOException, InterruptedException {
        manager.createTask(new Task("Задача 1", "Описание", Status.IN_PROGRESS,
                LocalDateTime.of(2022, 4,24, 12, 0), duration));
        manager.createTask(new Task("Задача 2", "Описание", Status.DONE));
        URI url = URI.create("http://localhost:8080/tasks/task/");
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "Сервер не удалил задачи");
        assertEquals(0, manager.getTaskList().size(), "Задачи не удалены");
    }

    @Test
    public void deleteAllEpicViaAPI() throws IOException, InterruptedException {
        manager.createEpic(new Epic("Эпик 1", "Описание 1", Status.DONE));
        manager.createEpic(new Epic("Эпик 2", "Описание 2", Status.IN_PROGRESS));
        URI url = URI.create("http://localhost:8080/tasks/epic/");
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "Сервер не удалил эпики");
        assertEquals(0, manager.getEpicList().size(), "Эпики не удалены");
    }

    @Test
    public void deleteAllSubTaskViaAPI() throws IOException, InterruptedException {
        long epicId = manager.createEpic(new Epic("Эпик 1", "Описание 1", Status.DONE));
        manager.createSubTask(new SubTask("Подзадача 1 Эпика 1","Описание", Status.NEW
                , LocalDateTime.of(2022, 4,23, 12, 0), duration, epicId));
        manager.createSubTask(new SubTask("Подзадача 2 Эпика 1","Описание", Status.DONE
                , epicId));
        URI url = URI.create("http://localhost:8080/tasks/subtask/");
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "Сервер не удалил подзадачи");
        assertEquals(0, manager.getSubTaskList().size(), "Подзадачи не удалены");
    }

    @Test
    public void deleteTaskByIdViaAPI() throws IOException, InterruptedException {
        long epicId = manager.createEpic(new Epic("Эпик 1", "Описание 1", Status.DONE));
        long subTaskId = manager.createSubTask(new SubTask("Подзадача 1 Эпика 1","Описание", Status.NEW
                , LocalDateTime.of(2022, 4,23, 12, 0), duration, epicId));
        long taskId = manager.createTask(new Task("Задача 1", "Описание", Status.IN_PROGRESS,
                LocalDateTime.of(2022, 4,24, 12, 0), duration));
        URI url = URI.create("http://localhost:8080/tasks/task/?id=" + taskId);
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "Сервер не удалил задачу");
        assertEquals(0, manager.getTaskList().size(), "Задача не удалена");

        url = URI.create("http://localhost:8080/tasks/subtask/?id=" + subTaskId);
        request = HttpRequest.newBuilder().uri(url).DELETE().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "Сервер не удалил подзадачу");
        assertEquals(0, manager.getSubTaskList().size(), "Подзадача не удалена");

        url = URI.create("http://localhost:8080/tasks/epic/?id=" + epicId);
        request = HttpRequest.newBuilder().uri(url).DELETE().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "Сервер не удалил эпик");
        assertEquals(0, manager.getEpicList().size(), "Эпик не удален");
    }

    @Test
    public void getEpicSubTasksFromAPI() throws IOException, InterruptedException {
        long epicId = manager.createEpic(new Epic("Эпик 1", "Описание 1", Status.DONE));
        long subTask1Id = manager.createSubTask(new SubTask("Подзадача 1 Эпика 1","Описание", Status.NEW
                , LocalDateTime.of(2022, 4,23, 12, 0), duration, epicId));
        long subTask2Id = manager.createSubTask (new SubTask("Подзадача 2 Эпика 1","Описание",
                Status.DONE, epicId));
        URI url = URI.create("http://localhost:8080/tasks/subtask/epic/?id=" + epicId);
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        JsonElement jsonElement = JsonParser.parseString(response.body());
        List<SubTask> subTaskList = gson.fromJson(jsonElement.toString(), new TypeToken<List<SubTask>>(){}.getType());
        assertEquals(manager.getByID(subTask1Id), subTaskList.get(0), "Подзадачи не совпадают");
        assertEquals(manager.getByID(subTask2Id), subTaskList.get(1), "Подзадачи не совпадают");
    }

    @Test
    public void getHistoryFromAPI() throws IOException, InterruptedException {
        long epicId = manager.createEpic(new Epic("Эпик 1", "Описание 1", Status.DONE));
        long subTask1Id = manager.createSubTask(new SubTask("Подзадача 1 Эпика 1","Описание", Status.NEW
                , LocalDateTime.of(2022, 4,23, 12, 0), duration, epicId));
        long taskId = manager.createTask(new Task("Задача 1", "Описание", Status.IN_PROGRESS,
                LocalDateTime.of(2022, 4,24, 12, 0), duration));
        manager.getByID(epicId);
        manager.getByID(subTask1Id);
        manager.getByID(taskId);
        URI url = URI.create("http://localhost:8080/tasks/history");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        JsonElement jsonElement = JsonParser.parseString(response.body());
        List<Task> history = gson.fromJson(jsonElement.toString(), new TypeToken<List<Task>>(){}.getType());
        assertEquals(manager.history().size(), history.size(), "Размер списка истории не совпадает");
        assertEquals(manager.history().get(0).getTitle(), history.get(0).getTitle(),
                "0 объект истории не совпадает");
        assertEquals(manager.history().get(2).getTitle(), history.get(2).getTitle(),
                "2 объект истории не совпадает");
    }

    @Test
    public void getPrioritizedTasksFromAPI() throws IOException, InterruptedException {
        long task1Id = manager.createTask(new Task("Задача 1", "Описание", Status.IN_PROGRESS,
                LocalDateTime.of(2022, 5,5, 12, 0), duration));
        long task2Id = manager.createTask(new Task("Задача 1", "Описание", Status.NEW,
                LocalDateTime.of(2022, 5,7, 12, 0), duration));
        long task3Id = manager.createTask(new Task("Задача 1", "Описание", Status.NEW));
        URI url = URI.create("http://localhost:8080/tasks/");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        JsonElement jsonElement = JsonParser.parseString(response.body());
        Collection<Task> prioritizedTasks = gson.fromJson(jsonElement.toString(),
                new TypeToken<Collection<Task>>(){}.getType());
        assertEquals(manager.getPrioritizedTasks().size(), prioritizedTasks.size(),
                "Размер списка не совпадает");
        assertEquals(manager.getPrioritizedTasks().toArray()[0], prioritizedTasks.toArray()[0],
                "Порядок задач не совпадает");
        assertEquals(manager.getPrioritizedTasks().toArray()[1], prioritizedTasks.toArray()[1],
                "Порядок задач не совпадает");
        assertEquals(manager.getPrioritizedTasks().toArray()[2], prioritizedTasks.toArray()[2],
                "Порядок задач не совпадает");
    }

}
