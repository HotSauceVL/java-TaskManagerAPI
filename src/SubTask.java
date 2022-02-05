public class SubTask extends Task {
    private int epicID;

    public SubTask(String title, String description, String status, int epicID) {
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
