package company.vk.edu.distrib.compute.sovesti.urlshortener.handler;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

import com.sun.net.httpserver.HttpExchange;

public final class HttpBody {

    private final HttpExchange exchange;

    public HttpBody(HttpExchange exchange) {
        this.exchange = Objects.requireNonNull(exchange);
    }

    public String decode() throws IOException {
        return new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
    }

}
