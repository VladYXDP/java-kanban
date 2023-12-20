package server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import gson.LocalDateTimeAdapter;
import manager.Managers;
import manager.task.TaskManager;
import task.Epic;
import task.Subtask;
import task.Task;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class HttpTaskServer {

    private static TaskManager manager;

    private static final int PORT = 8080;
    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
    private static Gson gson;

    public static void main(String[] args) throws IOException {
        HttpServer httpServer = HttpServer.create();
        httpServer.bind(new InetSocketAddress(PORT), 0);
        httpServer.createContext("/tasks", new TaskHandler());
        httpServer.start();

        manager = Managers.getFileManager();
        gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .create();

        System.out.println("HTTP-сервер запущен на " + PORT + " порту!");
    }

    private static class TaskHandler implements HttpHandler {

        @Override
        public void handle(HttpExchange exchange) throws IOException {

            String requestMethod = exchange.getRequestMethod();
            URI uri = exchange.getRequestURI();
            String[] pathParams = uri.getPath().split("/");
            String queryParams = uri.getQuery();
            Optional<String> response;
            switch (requestMethod) {
                case "GET":
                    response = createResponseBody(pathParams, queryParams);
                    if (response.isPresent()) {
                        sendResponse(exchange, response.get(), 200);
                    }
                    sendResponse(exchange, "", 200);
                    return;
                case "POST":
                    break;
            }
        }

        private String getAllTasks() {
            List<Task> tasks = manager.getAllTask();
            return gson.toJson(tasks);
        }

        private String getTaskById(int id) {
            Task task = manager.getTask(id);
            if (task == null) {
                return "";
            }
            return gson.toJson(task);
        }

        private String deleteTaskById(int id) {
            manager.removeTaskById(id);
            return "";
        }

        private String deleteAllTask() {
            manager.removeAllTask();
            return "";
        }

        private void updateTask(String jsonTask) {
            Task task = gson.fromJson(jsonTask, Task.class);
            manager.updateTask(task);
        }

        private String getAllEpic() {
            List<Epic> epics = manager.getAllEpic();
            return gson.toJson(epics);
        }

        private String getEpicById(int id) {
            Epic epic = manager.getEpic(id);
            if (epic == null) {
                return "";
            }
            return gson.toJson(epic);
        }

        private String removeEpicById(int id) {
            manager.removeEpicById(id);
            return "";
        }

        private String removeAllEpic() {
            manager.removeAllEpic();
            return "";
        }

        private void updateEpic(String jsonEpic) {
            Epic epic = gson.fromJson(jsonEpic, Epic.class);
            manager.updateEpic(epic);
        }

        private String getAllSubtask() {
            List<Subtask> subtasks = manager.getAllSubtask();
            return gson.toJson(subtasks);
        }

        private String getSubtaskById(int id) {
            Subtask subtask = manager.getSubtask(id);
            if (subtask == null) {
                return "";
            }
            return gson.toJson(subtask);
        }

        private String getSubtasksByEpicId(int id) {
            Epic epic = manager.getEpic(id);
            List<Subtask> subtasks = manager.getSubtaskByEpic(epic);
            return gson.toJson(subtasks);
        }

        private String removeSubtaskById(int id) {
            manager.removeSubtaskById(id);
            return "";
        }

        private String removeAllSubtask() {
            manager.removeAllSubtask();
            return "";
        }

        private void updateSubtask(String jsonSubtask) {
            Subtask subtask = gson.fromJson(jsonSubtask, Subtask.class);
            manager.updateSubtask(subtask);
        }

        private String getHistory() {
            List<Task> history = manager.getHistory();
            return gson.toJson(history);
        }

        private String getPrioritizedTasks() {
            List<Task> prioritizedTasks = manager.getPrioritizedTasks();
            return gson.toJson(prioritizedTasks);
        }

        private Optional<String> createResponseBody(String[] pathParam, String queryParams) {
            String responseBody = null;
            Map<String, String> queryParameters = Utils.queryToMap(queryParams);
            if (pathParam.length == 2 && pathParam[1].equals("tasks")) {
                responseBody = getPrioritizedTasks();
            } else if (pathParam.length >= 3 && pathParam[1].equals("tasks")) {
                if (pathParam[2].equals("history")) {
                    responseBody = getHistory();
                }
                if (pathParam[2].equals("task")) {
                    if (queryParameters != null) {
                        if (queryParameters.containsKey("id")) {
                            int id = Integer.parseInt(queryParameters.get("id"));
                            responseBody = getTaskById(id);
                        }
                    } else {
                        responseBody = getAllTasks();
                    }
                } else if (pathParam[2].equals("epic")) {
                    if (queryParameters != null) {
                        if (queryParameters.containsKey("id")) {
                            int id = Integer.parseInt(queryParameters.get("id"));
                            responseBody = getEpicById(id);
                        }
                    } else {
                        responseBody = getAllEpic();
                    }
                } else if (pathParam[2].equals("subtask")) {
                    if (pathParam.length == 4 && pathParam[3].equals("epic")) {
                        if (queryParameters != null) {
                            if (queryParameters.containsKey("id")) {
                                int id = Integer.parseInt(queryParameters.get("id"));
                                responseBody = getSubtasksByEpicId(id);
                            }
                        }
                    } else if (queryParameters != null) {
                        if (queryParameters.containsKey("id")) {
                            int id = Integer.parseInt(queryParameters.get("id"));
                            responseBody = getSubtaskById(id);
                        }
                    } else {
                        responseBody = getAllSubtask();
                    }
                }
            }
            if (responseBody == null || responseBody.isEmpty()) {
                return Optional.empty();
            }
            return Optional.of(responseBody);
        }

        private void sendResponse(HttpExchange exchange, String responseBody, int rCode) throws IOException {
            exchange.sendResponseHeaders(rCode, 0);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(responseBody.getBytes());
            }
        }
    }
}
