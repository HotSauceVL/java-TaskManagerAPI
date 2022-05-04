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
        private final Gson gson = new GsonBuilder()
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
                    if (path.length == 2) {
                        response = gson.toJson(taskManager.getPrioritizedTasks());
                        rCode = 200;
                    }
                    if (path.length == 3) {
                        if (httpExchange.getRequestURI().getPath().endsWith("history")) {
                            response = gson.toJson(taskManager.history());
                        } else {
                            response = getTask(getTaskType(path), extractIdFromQuery(query));
                        }
                        rCode = 200;
                    }
                    if (path.length == 4 && httpExchange.getRequestURI().getPath().endsWith("epic/")) {
                        response = gson.toJson(taskManager.getEpicSubTasks(extractIdFromQuery(query)));
                        rCode = 200;
                    }
                    break;
                case "POST":
                    InputStream inputStream = httpExchange.getRequestBody();
                    String body = new String(inputStream.readAllBytes(), Charset.defaultCharset());
                    Task task;
                    switch (getTaskType(path)) {
                        case TASK:
                            task = gson.fromJson(body, Task.class);
                            rCode = postTask(getTaskType(path), extractIdFromQuery(query), task);
                            break;
                        case SUBTASK:
                            task = gson.fromJson(body, SubTask.class);
                            rCode = postTask(getTaskType(path), extractIdFromQuery(query), task);
                            break;
                        case EPIC:
                            task = gson.fromJson(body, Epic.class);
                            rCode = postTask(getTaskType(path), extractIdFromQuery(query), task);
                            break;
                    }
                    break;
                case "DELETE":
                    rCode = deleteTask(getTaskType(path), extractIdFromQuery(query));
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
            return response;
        }

        private int postTask(TaskType taskType, long id, Task task) {
            int rCode = 400;
            if (id == 0) {
                switch (taskType) {
                    case TASK:
                        taskManager.createTask(task);
                        rCode = 201;
                        break;
                    case SUBTASK:
                        taskManager.createSubTask((SubTask) task);
                        rCode = 201;
                        break;
                    case EPIC:
                        taskManager.createEpic((Epic) task);
                        rCode = 201;
                        break;
                }
            } else {
                switch (taskType) {
                    case TASK:
                        taskManager.updateTask(id, task);
                        rCode = 201;
                        break;
                    case SUBTASK:
                        taskManager.updateSubTask(id, (SubTask) task);
                        rCode = 201;
                        break;
                    case EPIC:
                        taskManager.updateEpic(id, (Epic) task);
                        rCode = 201;
                        break;
                }
            }

            return rCode;
        }

        private int deleteTask(TaskType taskType, long id) {
            int rCode = 400;
            if (id != 0) {
                taskManager.deleteByID(id);
                rCode = 200;
            } else
                switch (taskType) {
                    case TASK:
                        taskManager.deleteAllTask();
                        rCode = 200;
                        break;
                    case SUBTASK:
                        taskManager.deleteAllSubTask();
                        rCode = 200;
                        break;
                    case EPIC:
                        taskManager.deleteAllEpic();
                        rCode = 200;
                        break;
                }
            return rCode;
        }

        private TaskType getTaskType(String[] path) {
            return TaskType.valueOf(path[2].toUpperCase());
        }
        private long extractIdFromQuery(String query) {
            if (query != null) {
                String[] split = query.split("=");
                return Long.parseLong(split[1]);
            } else
                return 0;
        }
}



