import server.HttpTaskServer;
import server.kv.KVServer;

import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException {
        new KVServer().start();
        new HttpTaskServer().start();
    }
}
