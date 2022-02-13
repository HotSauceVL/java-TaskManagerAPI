package tasks;

public class SubTask extends Task {
    private final int epicID;

    public SubTask(String title, String description, Status status, int epicID) {
        super(title, description, status);
        this.epicID = epicID;
    }

    public int getEpicID() {
        return epicID;
    }

    @Override
    public String toString() {
        return  super.toString() +
                "SubTask{" +
                "epicID=" + epicID +
                '}';
    }
}
