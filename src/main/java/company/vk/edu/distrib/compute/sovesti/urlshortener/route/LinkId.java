package company.vk.edu.distrib.compute.sovesti.urlshortener.route;

import java.util.Optional;

import com.sun.net.httpserver.HttpExchange;

import company.vk.edu.distrib.compute.sovesti.urlshortener.handler.ExchangeAttribute;
import company.vk.edu.distrib.compute.sovesti.urlshortener.handler.ExchangeAttributes;

final class LinkId implements ExchangeAttribute<String> {

    @Override
    public String key() {
        return "link_id";
    }

    @Override
    public Class<String> type() {
        return String.class;
    }

    String find(HttpExchange exchange) {
        return new RandomId().throwIfInvalid(new ExchangeAttributes(exchange).find(this).get());
    }

    void put(Optional<String> parsed, HttpExchange exchange) {
        parsed.ifPresent(id -> new ExchangeAttributes(exchange).put(this, id));
    }
}
