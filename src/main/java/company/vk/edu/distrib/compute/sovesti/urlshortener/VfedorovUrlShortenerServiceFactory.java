package company.vk.edu.distrib.compute.sovesti.urlshortener;

import java.io.IOException;
import java.net.InetSocketAddress;

import com.sun.net.httpserver.HttpServer;

import company.vk.edu.distrib.compute.AbstractHttpServiceFactory;

public final class VfedorovUrlShortenerServiceFactory extends AbstractHttpServiceFactory<VfedorovUrlShortenerService> {

    @Override
    protected VfedorovUrlShortenerService doCreate(int port) throws IOException {
        return new VfedorovUrlShortenerService(HttpServer.create(new InetSocketAddress(port), 0));
    }

}
