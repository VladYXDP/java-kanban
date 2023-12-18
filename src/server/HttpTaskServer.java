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

import java.awt.*;
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
            Optional<String> response;
            Map<String, String> queryParams = Utils.queryToMap(uri.getQuery());
            switch (requestMethod) {
                case "GET":
                    String[] pathParams = uri.getPath().split("/");
                    response = createResponseBodyForGetMethod(pathParams[2]);
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

        private String getAllEpic() {
            List<Epic> epics = manager.getAllEpic();
            return gson.toJson(epics);
        }

        private String getAllSubtask() {
            List<Subtask> subtasks = manager.getAllSubtask();
            return gson.toJson(subtasks);
        }

        private Optional<String> createResponseBodyForGetMethod(String pathParam) {
            String responseBody = null;
            if (pathParam.equals("tasks")) {
                responseBody = getAllTasks();
            }
            if (pathParam.equals("epics")) {
                responseBody = getAllEpic();
            }
            if (pathParam.equals("epics")) {
                responseBody = getAllEpic();
            }
            if (pathParam.equals("subtasks")) {
                responseBody = getAllSubtask();
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
