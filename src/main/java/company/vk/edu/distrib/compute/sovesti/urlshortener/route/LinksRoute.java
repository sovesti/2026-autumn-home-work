package company.vk.edu.distrib.compute.sovesti.urlshortener.route;

import java.io.IOException;
import java.net.URI;
import java.util.Objects;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import company.vk.edu.distrib.compute.Dao;
import company.vk.edu.distrib.compute.sovesti.urlshortener.handler.HandlersSwitch;
import company.vk.edu.distrib.compute.sovesti.urlshortener.handler.Http;
import company.vk.edu.distrib.compute.sovesti.urlshortener.handler.HttpBody;
import company.vk.edu.distrib.compute.sovesti.urlshortener.handler.PathElements;
import company.vk.edu.distrib.compute.sovesti.urlshortener.handler.Response;
import company.vk.edu.distrib.compute.sovesti.urlshortener.handler.ResponseBody;

public final class LinksRoute implements HttpRoute {

    private final Dao<String> links;
    private final RandomId ids = new RandomId();

    public LinksRoute(Dao<String> links) {
        this.links = Objects.requireNonNull(links);
    }

    @Override
    public String prefix() {
        return "/v0/links";
    }

    @Override
    public HttpHandler handler() {
        return new HandlersSwitch() //
            .with(Http.Method.POST, this::post) //
            .with(Http.Method.GET, this::get) //
            .with(Http.Method.PUT, this::put) //
            .with(Http.Method.DELETE, this::delete);
    }

    private void post(HttpExchange exchange) throws IOException {
        ensureHtmlUtf8(exchange);
        String id = ids.get();
        links.upsert(id, linkFromBody(exchange));
        respondHtmlUtf8(exchange);
        new Response(Http.StatusCode.Created, new ResponseBody.Plain(shortenedUrl(exchange, id))).accept(exchange);
    }

    private void get(HttpExchange exchange) throws IOException {
        respondHtmlUtf8(exchange);
        new Response(Http.StatusCode.OK, new ResponseBody.Plain(linkFromDao(exchange))).accept(exchange);
    }

    private void put(HttpExchange exchange) throws IOException {
        ensureHtmlUtf8(exchange);
        linkFromDao(exchange);
        links.upsert(linkId(exchange), linkFromBody(exchange));
        new Response(Http.StatusCode.OK).accept(exchange);
    }

    private void delete(HttpExchange exchange) throws IOException {
        links.delete(linkId(exchange));
        new Response(Http.StatusCode.Accepted).accept(exchange);
    }

    private void ensureHtmlUtf8(HttpExchange exchange) {
        new HtmlUtf8().orThrow(exchange);
    }

    private void respondHtmlUtf8(HttpExchange exchange) {
        new HtmlUtf8().respond(exchange);
    }

    private String shortenedUrl(HttpExchange exchange, String id) {
        return "http://%s/%s".formatted(exchange.getRequestHeaders().getFirst(Http.Header.Host), id);
    }

    private String linkFromBody(HttpExchange exchange) throws IOException {
        return URI.create(new HttpBody(exchange).decode()).toURL().toString();
    }

    private String linkFromDao(HttpExchange exchange) throws IOException {
        return links.get(linkId(exchange));
    }

    private String linkId(HttpExchange exchange) {
        return new LinkId().find(exchange);
    }

    @Override
    public void parsePath(HttpExchange exchange) {
        new LinkId().put(new PathElements(prefix()).apply(exchange).findFirst(), exchange);
    }

}
