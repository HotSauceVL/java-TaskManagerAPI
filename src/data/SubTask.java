package data;

public class SubTask extends Task {
    private final long epicID;

    public SubTask(String title, String description, Status status, long epicID) {
        super(title, description, status);
        this.epicID = epicID;
    }

    public long getEpicID() {
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
