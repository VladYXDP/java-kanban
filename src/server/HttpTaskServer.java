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
import java.util.List;
import java.util.Map;

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
            Map<String, String> queryParams = Utils.queryToMap(uri.getQuery());
            String responseBody = null;
            switch (requestMethod) {
                case "GET":
                    if (pathParams.length == 2 && pathParams[1].equals("tasks")) {
                        responseBody = getPrioritizedTasks();
                    } else if (pathParams.length >= 3 && pathParams[1].equals("tasks")) {
                        if (pathParams[2].equals("history")) {
                            responseBody = getHistory();
                        } else if (pathParams[2].equals("task")) {
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
                        } else if (pathParams[2].equals("epic")) {
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
                            } else if (queryParams != null) {
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
                    break;
                case "POST":
                    String requestBody = new String(exchange.getRequestBody().readAllBytes(), DEFAULT_CHARSET);
                    JsonElement jsonElement = JsonParser.parseString(requestBody);
                    if (!requestBody.isEmpty() && jsonElement.isJsonObject()) {
                        if (pathParams.length == 3) {
                            if (pathParams[1].equals("tasks")) {
                                if (pathParams[2].equals("task") && queryParams.containsKey("id")) {
                                    int id = Integer.parseInt(queryParams.get("id"));
                                    if (updateTask(requestBody, id)) {
                                        sendResponse(exchange, "", 201);
                                    } else {
                                        sendResponse(exchange, "", 400);
                                    }
                                } else {
                                    sendResponse(exchange, "", 404);
                                }
                            } else if (pathParams[2].equals("epic") && queryParams.containsKey("id")) {
                                int id = Integer.parseInt(queryParams.get("id"));
                                if (updateEpic(requestBody, id)) {
                                    sendResponse(exchange, "", 201);
                                } else {
                                    sendResponse(exchange, "", 400);
                                }
                            } else if (pathParams[2].equals("subtask") && queryParams.containsKey("id")) {
                                int id = Integer.parseInt(queryParams.get("id"));
                                if (updateSubtask(requestBody, id)) {
                                    sendResponse(exchange, "", 201);
                                } else {
                                    sendResponse(exchange, "", 400);
                                }
                            }
                        } else {
                            sendResponse(exchange, "", 404);
                        }
                    } else {
                        sendResponse(exchange, "", 400);
                    }
                    break;
                case "DELETE": {
                    if (pathParams.length == 3) {
                        if (pathParams[2].equals("task")) {
                            if (queryParams.containsKey("id")) {
                                int id = Integer.parseInt(queryParams.get("id"));
                                deleteTaskById(id);
                            } else if (queryParams.isEmpty()) {
                                deleteAllTask();
                                sendResponse(exchange, "", 200);
                            } else {
                                sendResponse(exchange, "", 404);
                            }
                        } else if (pathParams[2].equals("epic")) {
                            if (queryParams.containsKey("id")) {
                                int id = Integer.parseInt(queryParams.get("id"));
                                deleteEpicById(id);
                            } else if (queryParams.isEmpty()) {
                                deleteAllEpic();
                                sendResponse(exchange, "", 200);
                            } else {
                                sendResponse(exchange, "", 404);
                            }
                        } else if (pathParams[2].equals("subtask")) {
                            if (queryParams.containsKey("id")) {
                                int id = Integer.parseInt(queryParams.get("id"));
                                deleteSubtaskById(id);
                            } else if (queryParams.isEmpty()) {
                                deleteAllSubtask();
                                sendResponse(exchange, "", 200);
                            } else {
                                sendResponse(exchange, "", 404);
                            }
                        }
                    } else {
                        sendResponse(exchange, "", 404);
                    }
                    break;
                }
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

        private boolean updateTask(String jsonTask, int id) {
            Task oldTask = manager.getTask(id);
            Task newTask = gson.fromJson(jsonTask, Task.class);
            if (oldTask.equals(newTask)) {
                manager.updateTask(newTask);
                return true;
            }
            return false;
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

        private boolean updateEpic(String jsonEpic, int id) {
            Epic oldEpic = manager.getEpic(id);
            Epic newEpic = gson.fromJson(jsonEpic, Epic.class);
            if (oldEpic.equals(newEpic)) {
                manager.updateEpic(newEpic);
                return true;
            }
            return false;
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

        private String deleteSubtaskById(int id) {
            manager.removeSubtaskById(id);
            return "";
        }

        private String deleteAllSubtask() {
            manager.removeAllSubtask();
            return "";
        }

        private boolean updateSubtask(String jsonSubtask, int id) {
            Subtask oldSubtask = manager.getSubtask(id);
            Subtask newSubtask = gson.fromJson(jsonSubtask, Subtask.class);
            if (oldSubtask.equals(newSubtask)) {
                manager.updateSubtask(newSubtask);
                return true;
            }
            return false;
        }

        private String getHistory() {
            List<Task> history = manager.getHistory();
            return gson.toJson(history);
        }

        private String getPrioritizedTasks() {
            List<Task> prioritizedTasks = manager.getPrioritizedTasks();
            return gson.toJson(prioritizedTasks);
        }

        private void sendResponse(HttpExchange exchange, String responseBody, int rCode) throws IOException {
            exchange.sendResponseHeaders(rCode, 0);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(responseBody.getBytes());
            }
        }
    }
}
