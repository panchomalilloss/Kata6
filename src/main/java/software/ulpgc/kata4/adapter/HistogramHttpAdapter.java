package software.ulpgc.kata4.adapter;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import software.ulpgc.kata4.viewmodel.Histogram;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public class HistogramHttpAdapter implements HttpHandler {
    private final HistogramService service;

    public HistogramHttpAdapter(HistogramService service) {
        this.service = service;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            Map<String, String> params = Arrays.stream(
                        exchange.getRequestURI().getQuery().split("&"))
                    .map(p -> p.split("=", 2))
                    .collect(Collectors.toMap(p -> p[0], p -> p[1]));

            Histogram histogram = service.histogram(
                    params.get("attribute"),
                    Integer.parseInt(params.get("bin"))
            );

            String json = histogram.bins().stream()
                    .sorted()
                    .map(b -> "\"" + b + "\":" + histogram.count(b))
                    .collect(Collectors.joining(",", "{", "}"));

            byte[] response = json.getBytes(StandardCharsets.UTF_8);

            exchange.getResponseHeaders().set("Content-Type", "application/json");

            exchange.sendResponseHeaders(200, response.length);
            exchange.getResponseBody().write(response);
            exchange.close();
        } catch (Exception e){
            exchange.sendResponseHeaders(400, -1);
            exchange.close();
        }
    }
}
