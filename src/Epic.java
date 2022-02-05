import java.util.ArrayList;

public class Epic extends Task {
    private ArrayList<Integer> subTasks = new ArrayList<>();

    public Epic(String title, String description, String status) {
        super(title, description, status);
    }

    public ArrayList<Integer> getSubTasks() {
        return subTasks;
    }

    public void addSubTasks(int subTaskID) {
        subTasks.add(subTaskID);
    }

    public void deleteSubTask(Integer subTaskID) {
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
