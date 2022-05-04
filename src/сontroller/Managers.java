package сontroller;

import java.io.File;
import java.io.IOException;
import java.net.URI;

public class Managers {
    private final static TaskManager taskManager = new InMemoryTaskManager();
    private final static HistoryManager historyManager = new InMemoryHistoryManager();
    private final static FileBackedTasksManager fileBackedTasksManager =
            new FileBackedTasksManager(new File("src/data/TaskData.csv"));
    private final static HTTPTaskManager httpTaskManager;

    static {
        try {
            httpTaskManager = new HTTPTaskManager(URI.create("http://localhost:8078/"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public static TaskManager getDefaultTaskManager() {
        return httpTaskManager;
    }
    public static HistoryManager getDefaultHistoryManager() {
        return historyManager;
    }
    public static FileBackedTasksManager getDefaultFileBackedTasksManager() {
        return fileBackedTasksManager;
    }
}
