public class Main {
    public static void main(String[] args) {
        Manager manager = new Manager();

       int firstEpic = manager.createEpic(new Epic("Эпик 1", "Описание 1", "NEW"));
       int firstSubTask = manager.createSubTask(new SubTask("Подзадача 1",
               "Описание", "NEW", firstEpic));
       int secondSubTask = manager.createSubTask(new SubTask("Подзадача 2",
               "Описание", "DONE", firstEpic));

       int secondEpic = manager.createEpic(new Epic("Эпик 2", "Описание 2", "IN_PROGRESS"));
       int firstSubTaskSecondEpic = manager.createSubTask(new SubTask("Подзадача 1 второго эпика",
                "Описание", "DONE", secondEpic));

       int firstTask = manager.createTask(new Task("Задача 1", "Описание", "IN_PROGRESS"));
       int secondTask = manager.createTask(new Task("Задача 2", "Описание", "DONE"));

        System.out.println(manager.getEpicList());
        System.out.println(manager.getSubTaskList());
        System.out.println(manager.getTaskList());

        manager.updateEpic(firstEpic, new Epic("Эпик 1", "Описание 1", "DONE"));
        manager.updateSubTask(secondSubTask, new SubTask("Подзадача 1",
                "Описание", "DONE", secondEpic));
        manager.updateTask(secondTask, new Task ("Задача 2", "Описание", "NEW"));

        System.out.println(manager.getEpicList());
        System.out.println(manager.getSubTaskList());
        System.out.println(manager.getTaskList());

    }
}
