package company.vk.edu.distrib.compute.sovesti.urlshortener.handler;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Predicate;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.Request;

public final class HandlersSwitch implements HttpHandler {

    private final Map<Predicate<Request>, HttpHandler> handlers = new LinkedHashMap<>();

    public HandlersSwitch with(String method, HttpHandler handler) {
        return with(new MethodIs(method), handler);
    }

    public HandlersSwitch with(Predicate<Request> condition, HttpHandler handler) {
        handlers.put(condition, handler);
        return this;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        handlers.entrySet()
            .stream()
            .filter(entry -> entry.getKey().test(exchange))
            .map(Entry::getValue)
            .findFirst()
            .get()
            .handle(exchange);
    }

}
