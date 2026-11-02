package company.vk.edu.distrib.compute.sovesti.urlshortener.route;

import java.io.IOException;
import java.util.Objects;
import java.util.function.Function;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import company.vk.edu.distrib.compute.Dao;
import company.vk.edu.distrib.compute.sovesti.urlshortener.handler.ExchangeAttributes;
import company.vk.edu.distrib.compute.sovesti.urlshortener.handler.HandlersSwitch;
import company.vk.edu.distrib.compute.sovesti.urlshortener.handler.Response;
import company.vk.edu.distrib.compute.sovesti.urlshortener.handler.ResponseBody;
import company.vk.edu.distrib.compute.sovesti.urlshortener.http.StatusCodeConstants;

public final class EntitiesRoute implements HttpRoute {

    private final Function<HttpExchange, Dao<byte[]>> entities;

    public EntitiesRoute(Function<HttpExchange, Dao<byte[]>> entities) {
        this.entities = Objects.requireNonNull(entities);
    }

    @Override
    public String prefix() {
        return "/v0/entity";
    }

    @Override
    public HttpHandler handler() {
        return new HandlersSwitch().withGet(this::get).withPut(this::put).withDelete(this::delete);
    }

    private void get(HttpExchange exchange) throws IOException {
        new Response(StatusCodeConstants.OK, new ResponseBody.Plain(entities.apply(exchange)
            .get(new LinkId().find(exchange)))).accept(exchange);
    }

    private void put(HttpExchange exchange) throws IOException {
        entities.apply(exchange).upsert(new LinkId().find(exchange), exchange.getRequestBody().readAllBytes());
        new Response(StatusCodeConstants.CREATED).accept(exchange);
    }

    private void delete(HttpExchange exchange) throws IOException {
        entities.apply(exchange).delete(new LinkId().find(exchange));
        new Response(deleteStatus(exchange)).accept(exchange);
    }

    private int deleteStatus(HttpExchange exchange) {
        if (new ExchangeAttributes(exchange).find(new RequestAck()).isPresent()) {
            return StatusCodeConstants.OK;
        }
        return StatusCodeConstants.ACCEPTED;
    }

    @Override
    public void parsePath(HttpExchange exchange) {
        new LinkId().put(prefix(), exchange);
        new RequestAck().put(exchange);
    }

}
