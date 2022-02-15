package tasks;

import java.util.ArrayList;

public class Epic extends Task {
    private final ArrayList<Long> subTasks = new ArrayList<>();

    public Epic(String title, String description, Status status) {
        super(title, description, status);
    }

    public ArrayList<Long> getSubTasks() {
        return subTasks;
    }

    public void addSubTasks(long subTaskID) {
        subTasks.add(subTaskID);
    }

    public void deleteSubTask(long subTaskID) {
        subTasks.remove(subTaskID);
    }

    public void clearSubTasks () {
        subTasks.clear();
    }

    @Override
    public String toString() {
        return  super.toString() +
                "Epic{" +
                "subTasks=" + subTasks +
                '}';
    }
}
