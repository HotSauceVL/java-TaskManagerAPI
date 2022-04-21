package tests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import сontroller.InMemoryTaskManager;
import сontroller.Managers;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {
    @Override
    void setTaskManager () {
        taskManager = (InMemoryTaskManager) Managers.getDefaultTaskManager();
    }
}