package сontroller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import data.Epic;
import data.SubTask;
import data.Task;
import сontroller.serializers.EpicSerializer;
import сontroller.serializers.SubTaskSerializer;
import сontroller.serializers.TaskSerializer;

import java.io.File;
import java.io.IOException;
import java.net.URI;

public class HTTPTaskManager extends FileBackedTasksManager{
    private final URI serverUrl;
    private final KVTaskClient kvTaskClient;
    private Gson gson = new GsonBuilder()
            .registerTypeAdapter(Task.class, new TaskSerializer())
            .registerTypeAdapter(SubTask.class, new SubTaskSerializer())
            .registerTypeAdapter(Epic.class, new EpicSerializer())
            .create();

    public HTTPTaskManager(URI url) throws IOException, InterruptedException {
        this.serverUrl = url;
        kvTaskClient = new KVTaskClient(serverUrl);
    }
    @Override
    protected void save() {
        String tasksJson = gson.toJson(super.getTaskList());
        String epicsJson = gson.toJson(super.getEpicList());
        String subTaskJson = gson.toJson(super.getSubTaskList());
        String historyJson = gson.toJson(super.history());
        try {
            kvTaskClient.put("tasks", tasksJson);
            kvTaskClient.put("epics", epicsJson);
            kvTaskClient.put("subTasks", subTaskJson);
            kvTaskClient.put("history", historyJson);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
