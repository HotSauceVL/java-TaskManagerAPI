package сontroller;
import com.google.gson.*;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import data.Epic;
import data.SubTask;
import data.Task;
import data.TaskType;
import сontroller.serializers.*;


import java.io.*;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;

public class HttpTaskServer {
    HttpServer httpServer;
    private final TaskManager taskManager;
    private static final int PORT = 8080;

    public HttpTaskServer(TaskManager taskManager) throws IOException {
        httpServer = HttpServer.create(new InetSocketAddress(PORT), 0);
        this.taskManager = taskManager;
        httpServer.createContext("/tasks", new TasksHandler(taskManager));
    }



    public void start() {
        httpServer.start();
        System.out.println("HTTP-сервер запущен на " + PORT + " порту");
    }

    public void stop() {
        httpServer.stop(0);
    }
}

    class TasksHandler implements HttpHandler {
        TaskManager taskManager;
        private static final Gson gson = new GsonBuilder()
                .registerTypeAdapter(Task.class, new TaskSerializer())
                .registerTypeAdapter(SubTask.class, new SubTaskSerializer())
                .registerTypeAdapter(Epic.class, new EpicSerializer())
                .setPrettyPrinting()
                .create();

        int rCode = 404;
        public TasksHandler(TaskManager taskManager) throws IOException {
            this.taskManager = taskManager;
        }

        @Override
        public void handle(HttpExchange httpExchange) throws IOException {
            String response = "Что то пошло не так, проверьте путь для GET запроса или формат JSON для POST";
            String[] path = httpExchange.getRequestURI().getPath().split("/");
            String query = httpExchange.getRequestURI().getQuery();

            switch (httpExchange.getRequestMethod()) {
                case "GET":
                    if (path.length == 2 && httpExchange.getRequestURI().getPath().endsWith("tasks/")) {
                        response = gson.toJson(taskManager.getPrioritizedTasks());
                        rCode = 200;
                    }
                    if (path.length == 3 && (httpExchange.getRequestURI().getPath().endsWith("task/") ||
                            httpExchange.getRequestURI().getPath().endsWith("subtask/") ||
                            httpExchange.getRequestURI().getPath().endsWith("epic/")) ||
                            httpExchange.getRequestURI().getPath().endsWith("history")) {

                        if (httpExchange.getRequestURI().getPath().endsWith("history")) {
                            response = gson.toJson(taskManager.history());
                            rCode = 200;
                        } else {
                            response = getTask(getTaskType(path), extractIdFromQuery(query));
                            rCode = getRcode(200, response);
                        }
                    }
                    if (path.length == 4 && httpExchange.getRequestURI().getPath().endsWith("epic/")) {
                        try {
                            response = gson.toJson(taskManager.getEpicSubTasks(extractIdFromQuery(query)));
                            rCode = 200;
                        } catch (IllegalArgumentException exception) {
                            response = exception.getMessage();
                            rCode = 400;
                        }
                    }
                    break;
                case "POST":
                    InputStream inputStream = httpExchange.getRequestBody();
                    String body = new String(inputStream.readAllBytes(), Charset.defaultCharset());
                    Task task;
                    if (httpExchange.getRequestURI().getPath().endsWith("task/") ||
                            httpExchange.getRequestURI().getPath().endsWith("subtask/") ||
                            httpExchange.getRequestURI().getPath().endsWith("epic/")) {
                        switch (getTaskType(path)) {
                            case TASK:
                                task = gson.fromJson(body, Task.class);
                                response = postTask(getTaskType(path), extractIdFromQuery(query), task);
                                rCode = getRcode(201, response);
                                break;
                            case SUBTASK:
                                task = gson.fromJson(body, SubTask.class);
                                response = postTask(getTaskType(path), extractIdFromQuery(query), task);
                                rCode = getRcode(201, response);
                                break;
                            case EPIC:
                                task = gson.fromJson(body, Epic.class);
                                response = postTask(getTaskType(path), extractIdFromQuery(query), task);
                                rCode = getRcode(201, response);
                                break;
                        }
                    }
                    break;
                case "DELETE":
                    if (httpExchange.getRequestURI().getPath().endsWith("task/") ||
                            httpExchange.getRequestURI().getPath().endsWith("subtask/") ||
                            httpExchange.getRequestURI().getPath().endsWith("epic/")) {
                        response = deleteTask(getTaskType(path), extractIdFromQuery(query));
                        rCode = getRcode(200, response);
                    }
                    break;
                default:
                    System.out.println("Сервер не обрабатывает такие запросы" + httpExchange.getRequestMethod());
                    httpExchange.sendResponseHeaders(405, 0);
            }
            httpExchange.sendResponseHeaders(rCode, 0);

            try (OutputStream os = httpExchange.getResponseBody()) {
                os.write(response.getBytes());
            }
        }
        private String getTask(TaskType taskType, long id) {
            String response = "";
            try {
                if (id != 0) {
                    response = gson.toJson(taskManager.getByID(id));
                } else
                    switch (taskType) {
                        case TASK:
                            response = gson.toJson(taskManager.getTaskList());
                            break;
                        case SUBTASK:
                            response = gson.toJson(taskManager.getSubTaskList());
                            break;
                        case EPIC:
                            response = gson.toJson(taskManager.getEpicList());
                            break;
                    }
            } catch (IllegalArgumentException exception) {
                response = exception.getMessage();
            }
            return response;
        }

        private String postTask(TaskType taskType, long id, Task task) {
            String response = "Произошла ошибка, задача не добавлена";
            try {
                if (id == 0) {
                    switch (taskType) {
                        case TASK:
                            taskManager.createTask(task);
                            response = "Задача успешно добавлена";
                            break;
                        case SUBTASK:
                            taskManager.createSubTask((SubTask) task);
                            response = "Подзадача успешно добавлена";
                            break;
                        case EPIC:
                            taskManager.createEpic((Epic) task);
                            response = "Эпик успешно добавлен";
                            break;
                    }
                } else {
                    switch (taskType) {
                        case TASK:
                            taskManager.updateTask(id, task);
                            response = "Задача успешно обновлена";
                            break;
                        case SUBTASK:
                            taskManager.updateSubTask(id, (SubTask) task);
                            response = "Подзадача успешно обновлена";
                            break;
                        case EPIC:
                            taskManager.updateEpic(id, (Epic) task);
                            response = "Эпик успешно обновлен";
                            break;
                    }
                }
            } catch (IllegalArgumentException exception) {
                response = exception.getMessage();
            }
            return response;
        }

        private String deleteTask(TaskType taskType, long id) {
            String response = "Ошибка задача не удалена";
            try {
                if (id != 0) {
                    taskManager.deleteByID(id);
                    response = "Задача с id " + id + " успешно удалена";
                } else
                    switch (taskType) {
                        case TASK:
                            taskManager.deleteAllTask();
                            response = "Все задачи успешно удалены";
                            break;
                        case SUBTASK:
                            taskManager.deleteAllSubTask();
                            response = "Все подзадачи успешно удалены";
                            break;
                        case EPIC:
                            taskManager.deleteAllEpic();
                            response = "Все эпики успешно удалены";
                            break;
                    }
            } catch (IllegalArgumentException exception) {
                response = exception.getMessage();
            }
            return response;
        }

        private TaskType getTaskType(String[] path) {
            return TaskType.valueOf(path[2].toUpperCase());
        }
        private long extractIdFromQuery(String query) {
            if (query != null) {
                String[] split = query.split("=");
                if (split.length == 2)
                    return Long.parseLong(split[1]);
                else
                    return 0;
            } else
                return 0;
        }

        private int getRcode (int successfulRCode, String response)
        {
            if (response.contains("успешно")) {
                return successfulRCode;
            } else
                return 400;
        }
}



