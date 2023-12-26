package managers;

import manager.task.HttpTaskManager;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import server.kv.KVServer;

import java.io.IOException;

public class HttpTaskManagerTest extends TaskManagerTest<HttpTaskManager> {

    private final String KV_SERVER_URI = "http://localhost:8078/";
    private static KVServer kvServer;

    @BeforeAll
    public static void startKvServer() throws IOException {
        kvServer = new KVServer();
        kvServer.start();
    }

    @AfterAll
    public static void stopKvServer() {
        kvServer.stop();
    }

    @BeforeEach
    public void init() {
        try {
            manager = new HttpTaskManager(KV_SERVER_URI);
            manager.removeAllTask();
            manager.removeAllEpic();
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @AfterEach
    public void loadTasksFromKvServer() {
        try {
            manager.load();
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
