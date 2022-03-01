package data;

import java.util.Objects;

public class Node {
    private Task task;
    private Node nextNode;
    private Node previousNode;

    public Node(Task task) {
        this.task = task;
        this.nextNode = null;
        this.previousNode = null;
    }

    public Task getTask() {
        return task;
    }

    public void setTask(Task taskId) {
        this.task = taskId;
    }

    public Node getNextNode() {
        return nextNode;
    }

    public void setNextNode(Node nextNode) {
        this.nextNode = nextNode;
    }

    public Node getPreviousNode() {
        return previousNode;
    }

    public void setPreviousNode(Node previousNode) {
        this.previousNode = previousNode;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Node node = (Node) o;
        return Objects.equals(task, node.task) && Objects.equals(nextNode, node.nextNode) && Objects.equals(previousNode, node.previousNode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(task);
    }
}
