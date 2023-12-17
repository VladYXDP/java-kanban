package managers;

import manager.task.InMemoryTaskManager;
import org.junit.jupiter.api.BeforeEach;

public class InMemoryTaskTest extends TaskManagerTest<InMemoryTaskManager> {

    @BeforeEach
    public void init() {
        manager = new InMemoryTaskManager();
    }
}
