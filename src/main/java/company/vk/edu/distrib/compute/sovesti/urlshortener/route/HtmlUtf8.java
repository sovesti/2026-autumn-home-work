package company.vk.edu.distrib.compute.sovesti.urlshortener.route;

import java.util.Optional;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.Request;

import company.vk.edu.distrib.compute.sovesti.urlshortener.handler.Http;

final class HtmlUtf8 {

    void orThrow(Request request) {
        Optional.ofNullable(request.getRequestHeaders().getFirst(Http.Header.ContentType))
            .map(this::clean)
            .filter(clean(Http.ContentType.HtmlUtf8)::equals)
            .orElseThrow(IllegalArgumentException::new);
    }

    private String clean(String value) {
        return value.replace(" ", "").toLowerCase();
    }

    void respond(HttpExchange exchange) {
        exchange.getResponseHeaders().add(Http.Header.ContentType, Http.ContentType.HtmlUtf8);
    }

}
