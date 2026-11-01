package company.vk.edu.distrib.compute.sovesti.kv;

import java.net.http.HttpClient;
import java.util.Objects;

import com.sun.net.httpserver.HttpServer;

import company.vk.edu.distrib.compute.Dao;
import company.vk.edu.distrib.compute.kv.KVService;
import company.vk.edu.distrib.compute.sovesti.urlshortener.auth.AuthenticationScheme;
import company.vk.edu.distrib.compute.sovesti.urlshortener.dao.DaoFactory;
import company.vk.edu.distrib.compute.sovesti.urlshortener.dao.Daos;
import company.vk.edu.distrib.compute.sovesti.urlshortener.dao.InFileDaoFactory;
import company.vk.edu.distrib.compute.sovesti.urlshortener.handler.HttpContexts;
import company.vk.edu.distrib.compute.sovesti.urlshortener.route.EntitiesRoute;
import company.vk.edu.distrib.compute.sovesti.urlshortener.route.StatusRoute;

public final class VfedorovKVService implements KVService {

    private final int id;
    private final HttpServer server;
    private final Daos<byte[]> daos;

    public VfedorovKVService(int id, HttpServer server, DaoFactory<byte[]> factory) {
        this.id = id;
        this.server = Objects.requireNonNull(server);
        this.daos = new Daos<>(factory);
    }

    public VfedorovKVService(int id, HttpServer server) {
        this(id, server, new InFileDaoFactory());
    }

    @Override
    public void start() {
        Dao<byte[]> entities = daos.getOrCreate(key());
        HttpContexts contexts = new HttpContexts(server, new AuthenticationScheme.Transient());
        contexts.create(new StatusRoute());
        contexts.create(new EntitiesRoute(coordinated(entities)));
        server.start();
    }

    private ReplicatingClusterDao coordinated(Dao<byte[]> entities) {
        return new ReplicatingClusterDao(key(), id, entities, HttpClient.newHttpClient());
    }

    private String key() {
        return "entities-%d".formatted(id);
    }

    @Override
    public void stop() {
        daos.close();
        server.stop(1);
    }

    @Override
    public String toString() {
        return String.valueOf(id);
    }
}
