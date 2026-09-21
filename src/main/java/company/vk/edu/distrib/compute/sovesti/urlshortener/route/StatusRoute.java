package company.vk.edu.distrib.compute.sovesti.urlshortener.route;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import company.vk.edu.distrib.compute.sovesti.urlshortener.handler.HandlersSwitch;
import company.vk.edu.distrib.compute.sovesti.urlshortener.handler.Http;
import company.vk.edu.distrib.compute.sovesti.urlshortener.handler.Response;

public final class StatusRoute implements HttpRoute {

    @Override
    public String prefix() {
        return "/v0/status";
    }

    @Override
    public HttpHandler handler() {
        return new HandlersSwitch().with(Http.Method.GET, new Response(Http.StatusCode.OK));
    }

    @Override
    public void parsePath(HttpExchange exchange) {
    }

}
