package company.vk.edu.distrib.compute.sovesti.urlshortener.route;

import java.util.Objects;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import company.vk.edu.distrib.compute.Dao;
import company.vk.edu.distrib.compute.sovesti.urlshortener.handler.HandlersSwitch;
import company.vk.edu.distrib.compute.sovesti.urlshortener.handler.Http;
import company.vk.edu.distrib.compute.sovesti.urlshortener.handler.PathElements;
import company.vk.edu.distrib.compute.sovesti.urlshortener.handler.Response;

public final class RootRoute implements HttpRoute {

    private final Dao<String> links;

    public RootRoute(Dao<String> links) {
        this.links = Objects.requireNonNull(links);
    }

    @Override
    public String prefix() {
        return "/";
    }

    @Override
    public HttpHandler handler() {
        return new HandlersSwitch().with(Http.Method.GET, exchange -> {
            exchange.getResponseHeaders().add(Http.Header.Location, links.get(new LinkId().find(exchange)));
            new Response(Http.StatusCode.MovedPermanently).accept(exchange);
        });
    }

    @Override
    public void parsePath(HttpExchange exchange) {
        new LinkId().put(new PathElements(prefix()).apply(exchange).findFirst(), exchange);
    }
}
