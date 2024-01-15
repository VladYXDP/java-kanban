package client;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class KVTaskClient {

    private final String kvServerUri;
    private String token;
    private final String HEADER_ACCEPT = "Accept";
    private final String HEADER_ACCEPT_JSON = "application/json";

    public KVTaskClient(String uri) throws IOException, InterruptedException {
        this.kvServerUri = uri;
        getToken();
    }

    private void getToken() throws IOException, InterruptedException {
        URI registerUri = URI.create(kvServerUri + "register");

        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(registerUri)
                .version(HttpClient.Version.HTTP_1_1)
                .header(HEADER_ACCEPT, HEADER_ACCEPT_JSON)
                .build();

        HttpClient httpClient = HttpClient.newHttpClient();
        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
        HttpResponse<String> response = httpClient.send(request, handler);
        if (response.statusCode() == 200) {
            this.token = response.body();
        } else {
            throw new IOException("Ошибка обработки запроса! Получен код ответа: " + response.statusCode());
        }
    }

    public void put(String key, String json) throws IOException, InterruptedException {
        URI saveUri = URI.create(kvServerUri + "save/" + key + "?API_TOKEN=" + token);

        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .uri(saveUri)
                .version(HttpClient.Version.HTTP_1_1)
                .header(HEADER_ACCEPT, HEADER_ACCEPT_JSON)
                .build();

        HttpClient httpClient = HttpClient.newHttpClient();
        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
        HttpResponse<String> response = httpClient.send(request, handler);
        if (response.statusCode() != 200) {
            throw new IOException("Ошибка обработки запроса! Получен код ответа: " + response.statusCode());
        }
    }

    public String load(String key) throws IOException, InterruptedException {
        URI saveUri = URI.create(kvServerUri + "load/" + key + "?API_TOKEN=" + token);

        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(saveUri)
                .version(HttpClient.Version.HTTP_1_1)
                .header(HEADER_ACCEPT, HEADER_ACCEPT_JSON)
                .build();

        HttpClient httpClient = HttpClient.newHttpClient();
        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
        HttpResponse<String> response = httpClient.send(request, handler);
        if (response.statusCode() == 200) {
            return response.body();
        } else {
            throw new IOException("Ошибка обработки запроса! Получен код ответа: " + response.statusCode());
        }
    }
}
