package сontroller;

import java.io.File;

public class Managers {
    private final static TaskManager taskManager = new InMemoryTaskManager();
    private final static HistoryManager historyManager = new InMemoryHistoryManager();
    private final static FileBackedTasksManager fileBackedTasksManager =
            new FileBackedTasksManager(new File("src/data/TaskData.csv"));

    public static TaskManager getDefaultTaskManager() {
        return taskManager;
    }
    public static HistoryManager getDefaultHistoryManager() {
        return historyManager;
    }
    public static FileBackedTasksManager getDefaultFileBackedTasksManager() {
        return fileBackedTasksManager;
    }
}
