package client;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class KVTaskClient {

    private final String kvServerUri;
    private String token;

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
                .header("Accept", "application/json")
                .build();

        HttpClient httpClient = HttpClient.newHttpClient();
        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
        HttpResponse<String> response = httpClient.send(request, handler);
        this.token = response.body();
    }

    public void put(String key, String json) throws IOException, InterruptedException {
        URI saveUri = URI.create(kvServerUri + "save/" + key + "?API_TOKEN=" + token);

        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .uri(saveUri)
                .version(HttpClient.Version.HTTP_1_1)
                .header("Accept", "application/json")
                .build();

        HttpClient httpClient = HttpClient.newHttpClient();
        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
        httpClient.send(request, handler);
    }

    public String load(String key) throws IOException, InterruptedException {
        URI saveUri = URI.create(kvServerUri + "load/" + key + "?API_TOKEN=" + token);

        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(saveUri)
                .version(HttpClient.Version.HTTP_1_1)
                .header("Accept", "application/json")
                .build();

        HttpClient httpClient = HttpClient.newHttpClient();
        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
        HttpResponse<String> response = httpClient.send(request, handler);
        return response.body();
    }
}
