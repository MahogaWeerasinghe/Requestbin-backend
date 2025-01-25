import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpExchange;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.URL;

public class Main {
    public static void main(String[] args) throws Exception {
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
        server.createContext("/send-request", Main::handleRequest);
        server.setExecutor(null); // Use default executor
        System.out.println("Server running at http://localhost:8080/");
        server.start();
    }

    private static void handleRequest(HttpExchange exchange) throws IOException {
        if ("POST".equalsIgnoreCase(exchange.getRequestMethod())) {
            // Read the incoming JSON payload
            InputStream inputStream = exchange.getRequestBody();
            String body = new BufferedReader(new InputStreamReader(inputStream))
                    .lines()
                    .reduce("", (acc, line) -> acc + line);

            // Parse the input
            RequestData requestData = RequestData.fromJson(body);

            // Send the HTTP request
            ResponseData responseData = sendHttpRequest(requestData);

            // Write the response
            String response = responseData.toJson();
            exchange.sendResponseHeaders(200, response.getBytes().length);
            OutputStream outputStream = exchange.getResponseBody();
            outputStream.write(response.getBytes());
            outputStream.close();
        } else {
            exchange.sendResponseHeaders(405, -1); // Method not allowed
        }
    }

    private static ResponseData sendHttpRequest(RequestData requestData) {
        try {
            URL url = new URL(requestData.getUrl());
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod(requestData.getMethod());

            if (requestData.getBody() != null && !requestData.getBody().isEmpty()) {
                connection.setDoOutput(true);
                try (OutputStream os = connection.getOutputStream()) {
                    byte[] input = requestData.getBody().getBytes("utf-8");
                    os.write(input, 0, input.length);
                }
            }

            int statusCode = connection.getResponseCode();
            InputStream is = connection.getInputStream();
            String response = new BufferedReader(new InputStreamReader(is))
                    .lines()
                    .reduce("", (acc, line) -> acc + line);

            return new ResponseData(statusCode, connection.getResponseMessage(), response);

        } catch (Exception e) {
            return new ResponseData(500, "Internal Server Error", e.getMessage());
        }
    }
}
