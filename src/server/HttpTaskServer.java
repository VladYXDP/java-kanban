package server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HttpTaskServer {

    private TaskManager manager;

    private static final int PORT = 8080;
    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
    private static Gson gson;
    private HttpServer httpServer;

    public void start() throws IOException {
        httpServer = HttpServer.create();
        httpServer.bind(new InetSocketAddress(PORT), 0);
        httpServer.createContext("/tasks", new TaskHandler());
        httpServer.start();

        manager = Managers.getFileManager();
        gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .create();

        System.out.println("HTTP-сервер запущен на " + PORT + " порту!");
    }

    private class TaskHandler implements HttpHandler {

        @Override
        public void handle(HttpExchange exchange) {
            try {
                String requestMethod = exchange.getRequestMethod();
                URI uri = exchange.getRequestURI();
                String[] pathParams = uri.getPath().split("/");
                Map<String, String> queryParams = queryToMap(uri.getQuery());
                switch (requestMethod) {
                    case "GET":
                        handleGetQuery(exchange, pathParams, queryParams);
                        break;
                    case "POST":
                        handlePostQuery(exchange, pathParams, queryParams);
                        break;
                    case "DELETE": {
                        handleDeleteQuery(exchange, pathParams, queryParams);
                        break;
                    }
                }
            } catch (Exception e) {
                System.out.println("Ошибка во время обработки запроса: " + e);
            } finally {
                exchange.close();
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

        private void deleteTaskById(int id) {
            manager.removeTaskById(id);
        }

        private void deleteAllTask() {
            manager.removeAllTask();
        }

        private void updateTask(Task task) {
            manager.updateTask(task);
        }

        private void addTask(Task task) {
            manager.createTask(task);
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

        private void deleteEpicById(int id) {
            manager.removeEpicById(id);
        }

        private void deleteAllEpic() {
            manager.removeAllEpic();
        }

        private void updateEpic(Epic epic) {
            manager.updateEpic(epic);
        }

        private void addEpic(Epic epic) {
            manager.createEpic(epic);
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

        private void deleteSubtaskById(int id) {
            manager.removeSubtaskById(id);
        }

        private void deleteAllSubtask() {
            manager.removeAllSubtask();
        }

        private void updateSubtask(Subtask subtask) {
            manager.updateSubtask(subtask);
        }

        private void addSubtask(Subtask subtask) {
            manager.createSubtask(subtask);
        }

        private String getHistory() {
            List<Task> history = manager.getHistory();
            return gson.toJson(history);
        }

        private String getPrioritizedTasks() {
            List<Task> prioritizedTasks = manager.getPrioritizedTasks();
            return gson.toJson(prioritizedTasks);
        }

        private boolean checkQueryParams(Map<String, String> queryParams) {
            return queryParams == null || queryParams.isEmpty();
        }

        private int getIdFromQueryParam(Map<String, String> queryParams) {
            if (checkQueryParams(queryParams)) {
                return 0;
            } else {
                return Integer.parseInt(queryParams.get("id"));
            }
        }

        private boolean checkErrorCases(String[] pathParams, Map<String, String> queryParams) {
            return pathParams.length < 2 || pathParams.length > 4 || (pathParams.length == 3 && !checkQueryParams(queryParams)
                    && !queryParams.containsKey("id"));
        }

        private void sendResponse(HttpExchange exchange, String responseBody, int rCode) throws IOException {
            exchange.sendResponseHeaders(rCode, 0);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(responseBody.getBytes());
            }
        }

        private Map<String, String> queryToMap(String query) {
            if (query == null) {
                return null;
            }
            Map<String, String> result = new HashMap<>();
            for (String param : query.split("&")) {
                String[] entry = param.split("=");
                if (entry.length > 1) {
                    result.put(entry[0], entry[1]);
                } else {
                    result.put(entry[0], "");
                }
            }
            return result;
        }

        private void handleGetQuery(HttpExchange exchange, String[] pathParams, Map<String, String> queryParams) throws IOException {
            String responseBody = null;
            if (checkErrorCases(pathParams, queryParams)) {
                sendResponse(exchange, "", 404);
            } else if (pathParams.length >= 3) {
                int id = getIdFromQueryParam(queryParams);
                switch (pathParams[2]) {
                    case "task": {
                        if (id != 0) {
                            responseBody = getTaskById(id);
                        } else {
                            responseBody = getAllTasks();
                        }
                        break;
                    }
                    case "epic": {
                        if (id != 0) {
                            responseBody = getEpicById(id);
                        } else {
                            responseBody = getAllEpic();
                        }
                        break;
                    }
                    case "subtask": {
                        if (id != 0) {
                            if (pathParams.length == 4 && pathParams[3].equals("epic")) {
                                responseBody = getSubtasksByEpicId(id);
                            } else {
                                responseBody = getSubtaskById(id);
                            }
                        } else {
                            responseBody = getAllSubtask();
                        }
                        break;
                    }
                    case "history": {
                        responseBody = getHistory();
                        break;
                    }
                }
            }
            if (responseBody == null || responseBody.isEmpty()) {
                sendResponse(exchange, "", 204);
            } else {
                sendResponse(exchange, responseBody, 200);
            }
        }

        private void handleDeleteQuery(HttpExchange exchange, String[] pathParams, Map<String, String> queryParams) throws IOException {
            if (checkErrorCases(pathParams, queryParams)) {
                sendResponse(exchange, "", 404);
            } else if (pathParams.length >= 3) {
                int id = getIdFromQueryParam(queryParams);
                switch (pathParams[2]) {
                    case "task": {
                        if (id != 0) {
                            deleteTaskById(id);
                        } else {
                            deleteAllTask();
                        }
                        sendResponse(exchange, "", 200);
                        break;
                    }
                    case "epic": {
                        if (id != 0) {
                            deleteEpicById(id);
                        } else {
                            deleteAllEpic();
                        }
                        sendResponse(exchange, "", 200);
                        break;
                    }
                    case "subtask": {
                        if (id != 0) {
                            deleteSubtaskById(id);
                        } else {
                            deleteAllSubtask();
                        }
                        sendResponse(exchange, "", 200);
                        break;
                    }
                }
            }
        }

        private void handlePostQuery(HttpExchange exchange, String[] pathParams, Map<String, String> queryParams) throws IOException {
            String requestBody = new String(exchange.getRequestBody().readAllBytes(), DEFAULT_CHARSET);
            JsonElement jsonElement = JsonParser.parseString(requestBody);
            if (checkErrorCases(pathParams, queryParams)) {
                sendResponse(exchange, "", 404);
            } else if (requestBody.isEmpty() || !jsonElement.isJsonObject()) {
                sendResponse(exchange, "", 400);
            } else if (pathParams.length >= 3) {
                switch (pathParams[2]) {
                    case "task": {
                        Task task = gson.fromJson(requestBody, Task.class);
                        if (manager.getTask(task.getId()) != null) {
                            updateTask(task);
                        } else {
                            addTask(task);
                        }
                        sendResponse(exchange, "", 201);
                        break;
                    }
                    case "epic": {
                        Epic epic = gson.fromJson(requestBody, Epic.class);
                        if (manager.getEpic(epic.getId()) != null) {
                            updateEpic(epic);
                        } else {
                            addEpic(epic);
                        }
                        sendResponse(exchange, "", 201);
                        break;
                    }
                    case "subtask": {
                        Subtask subtask = gson.fromJson(requestBody, Subtask.class);
                        if (manager.getSubtask(subtask.getId()) != null) {
                            updateSubtask(subtask);
                        } else {
                            addSubtask(subtask);
                        }
                        sendResponse(exchange, "", 201);
                        break;
                    }
                }
            }
        }
    }

    public void stop() {
        httpServer.stop(5);
    }
}
