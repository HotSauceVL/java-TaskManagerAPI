package сontroller;

import java.io.File;
import java.net.URI;

public class Managers {
    private static TaskManager taskManager;
    private static HistoryManager historyManager;
    private static FileBackedTasksManager fileBackedTasksManager;
    private static HTTPTaskManager httpTaskManager;


    public static TaskManager getDefaultTaskManager() { // Спасибо за интересную информацию, даже не думал о таком=)
        try {
            if (httpTaskManager == null) {
                httpTaskManager = new HTTPTaskManager(URI.create("http://localhost:8078/"));
            }
            return httpTaskManager;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    public static HistoryManager getDefaultHistoryManager() {
        if (historyManager == null) {
            historyManager = new InMemoryHistoryManager();
        }
        return historyManager;
    }
    public static FileBackedTasksManager getDefaultFileBackedTasksManager() {
        if (fileBackedTasksManager == null) {
            fileBackedTasksManager = new FileBackedTasksManager(new File("src/data/TaskData.csv"));
        }
        return fileBackedTasksManager;
    }
}
