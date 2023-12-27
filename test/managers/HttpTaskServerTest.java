package managers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import gson.LocalDateTimeAdapter;
import org.junit.jupiter.api.*;
import server.HttpTaskServer;
import task.Epic;
import task.Subtask;
import task.Task;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

public class HttpTaskServerTest {

    private static final String HTTP_SERVER_URI = "http://localhost:8080/tasks/";
    private static HttpTaskServer httpTaskServer;
    private static Gson gson;

    private Task task1;
    private Task task2;
    private Task task3;
    private Task task4;

    private Epic epic1;
    private Epic epic2;
    private Epic epic3;

    private Subtask subtask1;
    private Subtask subtask2;
    private Subtask subtask3;
    private Subtask subtask4;
    private Subtask subtask5;

    @BeforeAll
    public static void startServer() throws IOException, InterruptedException {
        httpTaskServer = new HttpTaskServer();
        httpTaskServer.start();

        URI deleteAllTaskUri = URI.create(HTTP_SERVER_URI + "task/");
        URI deleteAllEpicUri = URI.create(HTTP_SERVER_URI + "epic/");
        URI deleteAllSubtaskUri = URI.create(HTTP_SERVER_URI + "subtask/");

        HttpRequest request = HttpRequest.newBuilder()
                .DELETE()
                .uri(deleteAllTaskUri)
                .version(HttpClient.Version.HTTP_1_1)
                .header("Accept", "application/json")
                .build();

        HttpClient httpClient = HttpClient.newHttpClient();
        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
        httpClient.send(request, handler);

        request = HttpRequest.newBuilder()
                .DELETE()
                .uri(deleteAllEpicUri)
                .version(HttpClient.Version.HTTP_1_1)
                .header("Accept", "application/json")
                .build();
        httpClient.send(request, handler);

        request = HttpRequest.newBuilder()
                .DELETE()
                .uri(deleteAllSubtaskUri)
                .version(HttpClient.Version.HTTP_1_1)
                .header("Accept", "application/json")
                .build();
        httpClient.send(request, handler);

        gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .create();
    }

    @BeforeEach
    public void createTasks() {
        task1 = new Task("Task name1", "Task desc1"
                , LocalDateTime.parse("2023-12-30T18:44:06.456050"), Duration.ofHours(8));
        task2 = new Task("Task name2", "Task desc2"
                , LocalDateTime.parse("2023-12-31T18:44:06.456050"), Duration.ofHours(8));
        task3 = new Task("Task name3", "Task desc3"
                , LocalDateTime.parse("2024-01-01T18:44:06.456050"), Duration.ofHours(8));
        task4 = new Task("Task name4", "Task desc4"
                , LocalDateTime.parse("2024-01-02T18:44:06.456050"), Duration.ofHours(8));

        epic1 = new Epic("Epic Name1", "Epic Desc1", LocalDateTime.parse("2024-01-04T18:44:06.456050"));
        epic2 = new Epic("Epic Name2", "Epic Desc2", LocalDateTime.parse("2024-01-05T18:44:06.456050"));
        epic3 = new Epic("Epic Name3", "Epic Desc3", LocalDateTime.parse("2024-01-06T18:44:06.456050"));

        subtask1 = new Subtask("Subtask Name1", "Subtask Desc1", 1, LocalDateTime.parse("2024-01-07T18:44:06.456050"),
                Duration.ofHours(10));
        subtask2 = new Subtask("Subtask Name2", "Subtask Desc2", 1, LocalDateTime.parse("2024-01-08T18:44:06.456050"),
                Duration.ofHours(10));
        subtask3 = new Subtask("Subtask Name3", "Subtask Desc3", 1, LocalDateTime.parse("2024-01-09T18:44:06.456050"),
                Duration.ofHours(10));
        subtask4 = new Subtask("Subtask Name4", "Subtask Desc4", 2, LocalDateTime.parse("2024-01-10T18:44:06.456050"),
                Duration.ofHours(10));
        subtask5 = new Subtask("Subtask Name5", "Subtask Desc5", 3, LocalDateTime.parse("2024-01-11T18:44:06.456050"),
                Duration.ofHours(10));
    }


    @Test
    public void getAllTaskTest() throws IOException, InterruptedException {
        URI getAllTaskUri = URI.create(HTTP_SERVER_URI + "task/");
        URI addTaskUri = URI.create(HTTP_SERVER_URI + "task/");
        HttpClient httpClient = HttpClient.newHttpClient();
        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
        List.of(task1, task2, task3).forEach(it -> {
            HttpRequest request = HttpRequest.newBuilder()
                    .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(it)))
                    .uri(addTaskUri)
                    .version(HttpClient.Version.HTTP_1_1)
                    .header("Accept", "application/json")
                    .build();
            try {
                httpClient.send(request, handler);
            } catch (IOException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        });

        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(getAllTaskUri)
                .version(HttpClient.Version.HTTP_1_1)
                .header("Accept", "application/json")
                .build();
        HttpResponse<String> response = httpClient.send(request, handler);
        List<Task> tasks = gson.fromJson(response.body(), new TypeToken<List<Task>>() {
        }.getType());
        Assertions.assertEquals(tasks.size(), 3);
    }

    @Test
    public void getTaskByIdTest() throws IOException, InterruptedException {
        URI getTaskByIdUri = URI.create(HTTP_SERVER_URI + "task/?id=4");
        URI addTaskUri = URI.create(HTTP_SERVER_URI + "task/");
        HttpClient httpClient = HttpClient.newHttpClient();
        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
        List.of(task1, task2, task3).forEach(it -> {
            HttpRequest request = HttpRequest.newBuilder()
                    .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(it)))
                    .uri(addTaskUri)
                    .version(HttpClient.Version.HTTP_1_1)
                    .header("Accept", "application/json")
                    .build();
            try {
                httpClient.send(request, handler);
            } catch (IOException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        });

        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(getTaskByIdUri)
                .version(HttpClient.Version.HTTP_1_1)
                .header("Accept", "application/json")
                .build();
        HttpResponse<String> response = httpClient.send(request, handler);
        Task task = gson.fromJson(response.body(), Task.class);
        Assertions.assertEquals(task.getId(), 4);
    }

    @Test
    public void deleteTaskByIdTest() throws IOException, InterruptedException {
        URI getAllTaskUri = URI.create(HTTP_SERVER_URI + "task/");
        URI addTaskUri = URI.create(HTTP_SERVER_URI + "task/");
        URI deleteTaskByIdUri = URI.create(HTTP_SERVER_URI + "task/?id=4");
        HttpClient httpClient = HttpClient.newHttpClient();
        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
        List.of(task1, task2, task3).forEach(it -> {
            HttpRequest request = HttpRequest.newBuilder()
                    .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(it)))
                    .uri(addTaskUri)
                    .version(HttpClient.Version.HTTP_1_1)
                    .header("Accept", "application/json")
                    .build();
            try {
                httpClient.send(request, handler);
            } catch (IOException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        });

        HttpRequest request = HttpRequest.newBuilder()
                .DELETE()
                .uri(deleteTaskByIdUri)
                .version(HttpClient.Version.HTTP_1_1)
                .header("Accept", "application/json")
                .build();
        httpClient.send(request, handler);

        request = HttpRequest.newBuilder()
                .GET()
                .uri(getAllTaskUri)
                .version(HttpClient.Version.HTTP_1_1)
                .header("Accept", "application/json")
                .build();
        HttpResponse<String> response = httpClient.send(request, handler);
        List<Task> task = gson.fromJson(response.body(), new TypeToken<List<Task>>() {
        }.getType());
        Assertions.assertEquals(task.size(), 2);
    }

    @Test
    public void deleteAllTaskTest() throws IOException, InterruptedException {
        URI getAllTaskUri = URI.create(HTTP_SERVER_URI + "task/");
        URI deleteAllTaskUri = URI.create(HTTP_SERVER_URI + "task/");
        URI addTaskUri = URI.create(HTTP_SERVER_URI + "task/");
        HttpClient httpClient = HttpClient.newHttpClient();
        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
        List.of(task1, task2, task3).forEach(it -> {
            HttpRequest request = HttpRequest.newBuilder()
                    .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(it)))
                    .uri(addTaskUri)
                    .version(HttpClient.Version.HTTP_1_1)
                    .header("Accept", "application/json")
                    .build();
            try {
                httpClient.send(request, handler);
            } catch (IOException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        });

        HttpRequest request = HttpRequest.newBuilder()
                .DELETE()
                .uri(deleteAllTaskUri)
                .version(HttpClient.Version.HTTP_1_1)
                .header("Accept", "application/json")
                .build();
        httpClient.send(request, handler);

        request = HttpRequest.newBuilder()
                .GET()
                .uri(getAllTaskUri)
                .version(HttpClient.Version.HTTP_1_1)
                .header("Accept", "application/json")
                .build();
        HttpResponse<String> response = httpClient.send(request, handler);
        List<Task> task = gson.fromJson(response.body(), new TypeToken<List<Task>>() {
        }.getType());
        Assertions.assertEquals(task.size(), 0);
    }

    @Test
    public void getAllEpicTest() throws IOException, InterruptedException {
        URI getAllEpicUri = URI.create(HTTP_SERVER_URI + "epic/");
        URI addEpicUri = URI.create(HTTP_SERVER_URI + "epic/");
        HttpClient httpClient = HttpClient.newHttpClient();
        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
        List.of(epic1, epic2, epic3).forEach(it -> {
            HttpRequest request = HttpRequest.newBuilder()
                    .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(it)))
                    .uri(addEpicUri)
                    .version(HttpClient.Version.HTTP_1_1)
                    .header("Accept", "application/json")
                    .build();
            try {
                httpClient.send(request, handler);
            } catch (IOException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        });

        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(getAllEpicUri)
                .version(HttpClient.Version.HTTP_1_1)
                .header("Accept", "application/json")
                .build();
        HttpResponse<String> response = httpClient.send(request, handler);
        List<Epic> epics = gson.fromJson(response.body(), new TypeToken<List<Epic>>() {
        }.getType());
        Assertions.assertEquals(epics.size(), 3);
    }

    @Test
    public void getEpicByIdTest() throws IOException, InterruptedException {
        URI getEpicByIdUri = URI.create(HTTP_SERVER_URI + "epic/?id=4");
        URI addEpicUri = URI.create(HTTP_SERVER_URI + "epic/");
        HttpClient httpClient = HttpClient.newHttpClient();
        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
        List.of(epic1, epic2, epic3).forEach(it -> {
            HttpRequest request = HttpRequest.newBuilder()
                    .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(it)))
                    .uri(addEpicUri)
                    .version(HttpClient.Version.HTTP_1_1)
                    .header("Accept", "application/json")
                    .build();
            try {
                httpClient.send(request, handler);
            } catch (IOException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        });

        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(getEpicByIdUri)
                .version(HttpClient.Version.HTTP_1_1)
                .header("Accept", "application/json")
                .build();
        HttpResponse<String> response = httpClient.send(request, handler);
        Epic epic = gson.fromJson(response.body(), Epic.class);
        Assertions.assertEquals(epic.getId(), 4);
    }

    @Test
    public void deleteEpicByIdTest() throws IOException, InterruptedException {
        URI getAllEpicUri = URI.create(HTTP_SERVER_URI + "epic/");
        URI addEpicUri = URI.create(HTTP_SERVER_URI + "epic/");
        URI deleteEpicByIdUri = URI.create(HTTP_SERVER_URI + "epic/?id=4");
        HttpClient httpClient = HttpClient.newHttpClient();
        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
        List.of(epic1, epic2, epic3).forEach(it -> {
            HttpRequest request = HttpRequest.newBuilder()
                    .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(it)))
                    .uri(addEpicUri)
                    .version(HttpClient.Version.HTTP_1_1)
                    .header("Accept", "application/json")
                    .build();
            try {
                httpClient.send(request, handler);
            } catch (IOException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        });

        HttpRequest request = HttpRequest.newBuilder()
                .DELETE()
                .uri(deleteEpicByIdUri)
                .version(HttpClient.Version.HTTP_1_1)
                .header("Accept", "application/json")
                .build();
        httpClient.send(request, handler);

        request = HttpRequest.newBuilder()
                .GET()
                .uri(getAllEpicUri)
                .version(HttpClient.Version.HTTP_1_1)
                .header("Accept", "application/json")
                .build();
        HttpResponse<String> response = httpClient.send(request, handler);
        List<Epic> epic = gson.fromJson(response.body(), new TypeToken<List<Epic>>() {
        }.getType());
        Assertions.assertEquals(epic.size(), 2);
    }

    @Test
    public void deleteAllEpicTest() throws IOException, InterruptedException {
        URI getAllEpicUri = URI.create(HTTP_SERVER_URI + "epic/");
        URI deleteAllEpicUri = URI.create(HTTP_SERVER_URI + "epic/");
        URI addEpicUri = URI.create(HTTP_SERVER_URI + "epic/");
        HttpClient httpClient = HttpClient.newHttpClient();
        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
        List.of(epic1, epic2, epic3).forEach(it -> {
            HttpRequest request = HttpRequest.newBuilder()
                    .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(it)))
                    .uri(addEpicUri)
                    .version(HttpClient.Version.HTTP_1_1)
                    .header("Accept", "application/json")
                    .build();
            try {
                httpClient.send(request, handler);
            } catch (IOException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        });

        HttpRequest request = HttpRequest.newBuilder()
                .DELETE()
                .uri(deleteAllEpicUri)
                .version(HttpClient.Version.HTTP_1_1)
                .header("Accept", "application/json")
                .build();
        httpClient.send(request, handler);

        request = HttpRequest.newBuilder()
                .GET()
                .uri(getAllEpicUri)
                .version(HttpClient.Version.HTTP_1_1)
                .header("Accept", "application/json")
                .build();
        HttpResponse<String> response = httpClient.send(request, handler);
        List<Epic> epics = gson.fromJson(response.body(), new TypeToken<List<Epic>>() {
        }.getType());
        Assertions.assertEquals(epics.size(), 0);
    }

    @Test
    public void getAllSubtaskTest() throws IOException, InterruptedException {
        URI addEpicUri = URI.create(HTTP_SERVER_URI + "epic/");
        URI addSubtaskUri = URI.create(HTTP_SERVER_URI + "subtask/");
        HttpClient httpClient = HttpClient.newHttpClient();
        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
        List.of(epic1, epic2, epic3).forEach(it -> {
            HttpRequest request = HttpRequest.newBuilder()
                    .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(it)))
                    .uri(addEpicUri)
                    .version(HttpClient.Version.HTTP_1_1)
                    .header("Accept", "application/json")
                    .build();
            try {
                httpClient.send(request, handler);
            } catch (IOException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(subtask1)))
                .uri(addSubtaskUri)
                .version(HttpClient.Version.HTTP_1_1)
                .header("Accept", "application/json")
                .build();
        httpClient.send(request, handler);

        request = HttpRequest.newBuilder()
                .GET()
                .uri(addSubtaskUri)
                .version(HttpClient.Version.HTTP_1_1)
                .header("Accept", "application/json")
                .build();
        HttpResponse<String> response = httpClient.send(request, handler);
        List<Subtask> subtasks = gson.fromJson(response.body(), new TypeToken<List<Subtask>>() {
        }.getType());
        Assertions.assertEquals(subtasks.size(), 3);
    }

    @Test
    public void getSubtaskByIdTest() throws IOException, InterruptedException {
        URI addEpicUri = URI.create(HTTP_SERVER_URI + "epic/");
        URI addSubtaskUri = URI.create(HTTP_SERVER_URI + "subtask/");
        URI getSubtaskUri = URI.create(HTTP_SERVER_URI + "subtask/?id=5");
        HttpClient httpClient = HttpClient.newHttpClient();
        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
        List.of(epic1, epic2, epic3).forEach(it -> {
            HttpRequest request = HttpRequest.newBuilder()
                    .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(it)))
                    .uri(addEpicUri)
                    .version(HttpClient.Version.HTTP_1_1)
                    .header("Accept", "application/json")
                    .build();
            try {
                httpClient.send(request, handler);
            } catch (IOException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(subtask1)))
                .uri(addSubtaskUri)
                .version(HttpClient.Version.HTTP_1_1)
                .header("Accept", "application/json")
                .build();
        httpClient.send(request, handler);

        request = HttpRequest.newBuilder()
                .GET()
                .uri(getSubtaskUri)
                .version(HttpClient.Version.HTTP_1_1)
                .header("Accept", "application/json")
                .build();
        HttpResponse<String> response = httpClient.send(request, handler);
        Subtask subtask = gson.fromJson(response.body(), Subtask.class);
        Assertions.assertEquals(subtask.getId(), 4);
    }

    @Test
    public void deleteSubtaskByIdTest() throws IOException, InterruptedException {
        URI addEpicUri = URI.create(HTTP_SERVER_URI + "epic/");
        URI addSubtaskUri = URI.create(HTTP_SERVER_URI + "subtask/");
        URI deleteSubtaskUri = URI.create(HTTP_SERVER_URI + "subtask/?id=5");
        HttpClient httpClient = HttpClient.newHttpClient();
        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
        List.of(epic1, epic2, epic3).forEach(it -> {
            HttpRequest request = HttpRequest.newBuilder()
                    .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(it)))
                    .uri(addEpicUri)
                    .version(HttpClient.Version.HTTP_1_1)
                    .header("Accept", "application/json")
                    .build();
            try {
                httpClient.send(request, handler);
            } catch (IOException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(subtask1)))
                .uri(addSubtaskUri)
                .version(HttpClient.Version.HTTP_1_1)
                .header("Accept", "application/json")
                .build();
        httpClient.send(request, handler);

        request = HttpRequest.newBuilder()
                .DELETE()
                .uri(deleteSubtaskUri)
                .version(HttpClient.Version.HTTP_1_1)
                .header("Accept", "application/json")
                .build();
        HttpResponse<String> response = httpClient.send(request, handler);
        List<Subtask> subtasks = gson.fromJson(response.body(), new TypeToken<List<Subtask>>() {
        }.getType());
        Assertions.assertEquals(subtasks.size(), 4);
    }

    @Test
    public void deleteAllSubtaskTest() throws IOException, InterruptedException {
        URI addEpicUri = URI.create(HTTP_SERVER_URI + "epic/");
        URI addSubtaskUri = URI.create(HTTP_SERVER_URI + "subtask/");
        URI deleteSubtaskUri = URI.create(HTTP_SERVER_URI + "subtask/");
        HttpClient httpClient = HttpClient.newHttpClient();
        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
        List.of(epic1, epic2, epic3).forEach(it -> {
            HttpRequest request = HttpRequest.newBuilder()
                    .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(it)))
                    .uri(addEpicUri)
                    .version(HttpClient.Version.HTTP_1_1)
                    .header("Accept", "application/json")
                    .build();
            try {
                httpClient.send(request, handler);
            } catch (IOException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(subtask1)))
                .uri(addSubtaskUri)
                .version(HttpClient.Version.HTTP_1_1)
                .header("Accept", "application/json")
                .build();
        httpClient.send(request, handler);

        request = HttpRequest.newBuilder()
                .DELETE()
                .uri(deleteSubtaskUri)
                .version(HttpClient.Version.HTTP_1_1)
                .header("Accept", "application/json")
                .build();
        HttpResponse<String> response = httpClient.send(request, handler);
        List<Subtask> subtasks = gson.fromJson(response.body(), new TypeToken<List<Subtask>>() {
        }.getType());
        Assertions.assertEquals(subtasks.size(), 0);
    }

    @AfterAll
    public static void stopServer() {
        httpTaskServer.stop();
    }
}
