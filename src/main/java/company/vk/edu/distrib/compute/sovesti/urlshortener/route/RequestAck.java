package company.vk.edu.distrib.compute.sovesti.urlshortener.route;

import java.text.DecimalFormat;
import java.text.ParsePosition;
import java.util.Optional;

import com.sun.net.httpserver.HttpExchange;

import company.vk.edu.distrib.compute.sovesti.urlshortener.handler.BadRequestException;
import company.vk.edu.distrib.compute.sovesti.urlshortener.handler.ExchangeAttribute;
import company.vk.edu.distrib.compute.sovesti.urlshortener.handler.ExchangeAttributes;

public final class RequestAck implements ExchangeAttribute<String> {

    @Override
    public String key() {
        return "ack";
    }

    @Override
    public Class<String> type() {
        return String.class;
    }

    public int find(HttpExchange exchange) {
        return new ExchangeAttributes(exchange).find(this).map(this::parse).orElse(1);
    }

    void put(HttpExchange exchange) {
        put(new ExchangeAttributes(exchange));
    }

    private void put(ExchangeAttributes attributes) {
        attributes.find(new QueryAttribute())
            .flatMap(q -> q.get("ack"))
            .ifPresent(ack -> attributes.put(this, ack));
    }

    private int parse(String ack) {
        int replicas = new ClusterDescription().length();
        return Optional.ofNullable(new DecimalFormat().parse(ack, new ParsePosition(0)))
            .map(Number::intValue)
            .filter(parsed -> 1 <= parsed && parsed <= replicas)
            .orElseThrow(() -> badRequest(ack, replicas));
    }

    private RuntimeException badRequest(String ack, int bound) {
        return new BadRequestException("Incorrect ack value: %s, expected decimal number between 1 and %d"
            .formatted(ack, bound));
    }
}
