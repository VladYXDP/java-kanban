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
        public void handle(HttpExchange exchange) throws IOException {
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
            if (pathParams.length == 2 && checkQueryParams(queryParams)) {
                responseBody = getPrioritizedTasks();
            } else if (pathParams.length >= 3) {
                if (pathParams[2].equals("history") && checkQueryParams(queryParams) && pathParams.length == 3) {
                    responseBody = getHistory();
                } else if (pathParams[2].equals("task") && pathParams.length == 3) {
                    if (queryParams != null) {
                        if (queryParams.containsKey("id")) {
                            int id = Integer.parseInt(queryParams.get("id"));
                            responseBody = getTaskById(id);
                        } else {
                            sendResponse(exchange, "", 404);
                        }
                    } else {
                        responseBody = getAllTasks();
                    }
                } else if (pathParams[2].equals("epic") && pathParams.length == 3) {
                    if (queryParams != null) {
                        if (queryParams.containsKey("id")) {
                            int id = Integer.parseInt(queryParams.get("id"));
                            responseBody = getEpicById(id);
                        } else {
                            sendResponse(exchange, "", 404);
                        }
                    } else {
                        responseBody = getAllEpic();
                    }
                } else if (pathParams[2].equals("subtask")) {
                    if (pathParams.length == 4 && pathParams[3].equals("epic")) {
                        if (queryParams != null) {
                            if (queryParams.containsKey("id")) {
                                int id = Integer.parseInt(queryParams.get("id"));
                                responseBody = getSubtasksByEpicId(id);
                            } else {
                                sendResponse(exchange, "", 404);
                            }
                        }
                    } else if (queryParams != null && pathParams.length == 3) {
                        if (queryParams.containsKey("id")) {
                            int id = Integer.parseInt(queryParams.get("id"));
                            responseBody = getSubtaskById(id);
                        } else {
                            sendResponse(exchange, "", 404);
                        }
                    } else {
                        responseBody = getAllSubtask();
                    }
                } else {
                    sendResponse(exchange, "", 404);
                }
            } else {
                sendResponse(exchange, "", 404);
            }
            if (responseBody == null || responseBody.isEmpty()) {
                sendResponse(exchange, "", 204);
            } else {
                sendResponse(exchange, responseBody, 200);
            }
        }

        private void handleDeleteQuery(HttpExchange exchange, String[] pathParams, Map<String, String> queryParams) throws IOException {
            if (pathParams.length == 3) {
                if (pathParams[2].equals("task")) {
                    if (checkQueryParams(queryParams)) {
                        deleteAllTask();
                        sendResponse(exchange, "", 200);
                    } else if (queryParams.containsKey("id")) {
                        int id = Integer.parseInt(queryParams.get("id"));
                        deleteTaskById(id);
                    } else {
                        sendResponse(exchange, "", 404);
                    }
                } else if (pathParams[2].equals("epic")) {
                    if (checkQueryParams(queryParams)) {
                        deleteAllEpic();
                        sendResponse(exchange, "", 200);
                    }
                    if (queryParams.containsKey("id")) {
                        int id = Integer.parseInt(queryParams.get("id"));
                        deleteEpicById(id);
                    } else {
                        sendResponse(exchange, "", 404);
                    }
                } else if (pathParams[2].equals("subtask")) {
                    if (checkQueryParams(queryParams)) {
                        deleteAllSubtask();
                        sendResponse(exchange, "", 200);
                    } else if (queryParams.containsKey("id")) {
                        int id = Integer.parseInt(queryParams.get("id"));
                        deleteSubtaskById(id);
                    } else {
                        sendResponse(exchange, "", 404);
                    }
                }
            } else {
                sendResponse(exchange, "", 404);
            }
        }

        private void handlePostQuery(HttpExchange exchange, String[] pathParams, Map<String, String> queryParams) throws IOException {
            String requestBody = new String(exchange.getRequestBody().readAllBytes(), DEFAULT_CHARSET);
            JsonElement jsonElement = JsonParser.parseString(requestBody);
            if (!requestBody.isEmpty() && jsonElement.isJsonObject()) {
                if (pathParams.length == 3 && checkQueryParams(queryParams)) {
                    if (pathParams[2].equals("task")) {
                        Task task = gson.fromJson(requestBody, Task.class);
                        if (manager.getTask(task.getId()) != null) {
                            updateTask(task);
                            sendResponse(exchange, "", 201);
                        } else {
                            addTask(task);
                            sendResponse(exchange, "", 201);
                        }
                    } else if (pathParams[2].equals("epic")) {
                        Epic epic = gson.fromJson(requestBody, Epic.class);
                        if (manager.getEpic(epic.getId()) != null) {
                            updateEpic(epic);
                            sendResponse(exchange, "", 201);
                        } else {
                            addEpic(epic);
                            sendResponse(exchange, "", 201);
                        }
                    } else if (pathParams[2].equals("subtask")) {
                        Subtask subtask = gson.fromJson(requestBody, Subtask.class);
                        if (manager.getSubtask(subtask.getId()) != null) {
                            updateSubtask(subtask);
                            sendResponse(exchange, "", 201);
                        } else {
                            addSubtask(subtask);
                            sendResponse(exchange, "", 201);
                        }
                    }
                } else {
                    sendResponse(exchange, "", 404);
                }
            } else {
                sendResponse(exchange, "", 400);
            }
        }
    }

    public void stop() {
        httpServer.stop(1000);
    }
}
