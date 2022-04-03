package сontroller;

import data.Node;
import data.Task;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

public class InMemoryHistoryManager implements HistoryManager {
    private static final Map<Long, Node> historyMap = new HashMap<>();
    private static Node headNode = null;
    private static Node tailNode = null;

    @Override
    public void add(Task task) {
       if (historyMap.containsKey(task.getId())){
           removeNode(historyMap.get(task.getId()));
           linkLast(task);
       } else {
           linkLast(task);
       }
    }

    @Override
    public List<Task> getHistory() {
        return getTasks();
    }

    @Override
    public void remove(Task task) {
        if (historyMap.containsKey(task.getId())) {
            removeNode(historyMap.get(task.getId()));
        }
    }


    public static void update(long id, Task newTask) {
        if (historyMap.containsKey(id)) {
            historyMap.get(id).setTask(newTask);
        }
    }

    private void linkLast(Task task) {
        if (headNode == null) {
            headNode = new Node(task);
            tailNode = headNode;
            historyMap.put(task.getId(), headNode);
        } else {
            Node newNode = new Node(task);
            tailNode.setNextNode(newNode);
            newNode.setPreviousNode(tailNode);
            tailNode = newNode;
            historyMap.put(task.getId(), tailNode);
        }
    }

    private ArrayList<Task> getTasks() {
        Node currentNode = headNode;
        ArrayList<Task> taskList = new ArrayList<>();

        while (currentNode != null) {
            taskList.add(currentNode.getTask());
            if (currentNode.getNextNode() == null)
                currentNode = null;
            else
                currentNode = currentNode.getNextNode();

        }
        return taskList;
    }

    private void removeNode(Node node) {
        if (headNode.equals(node)) {
            if (headNode.equals(tailNode)) {
                headNode = null;
                tailNode = null;
                historyMap.remove(node.getTask().getId());
            } else {
                  historyMap.remove(node.getTask().getId());
                  headNode = node.getNextNode();
                  headNode.setPreviousNode(null);
            }
        } else if (tailNode.equals(node)) {
                   historyMap.remove(node.getTask().getId());
                   tailNode = node.getPreviousNode();
                   tailNode.setNextNode(null);
               } else {
                     node.getNextNode().setPreviousNode(node.getPreviousNode());
                     node.getPreviousNode().setNextNode(node.getNextNode());
                     historyMap.remove(node.getTask().getId());
        }
    }


}


