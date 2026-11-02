package company.vk.edu.distrib.compute.sovesti.kv;

import java.io.IOException;
import java.util.List;

import com.sun.net.httpserver.HttpExchange;

import company.vk.edu.distrib.compute.Dao;
import company.vk.edu.distrib.compute.sovesti.urlshortener.route.ClusterDescription;
import company.vk.edu.distrib.compute.sovesti.urlshortener.route.RequestAck;

public record CoordinatedClusterDao(ClusterDao cluster, HttpExchange exchange) implements Dao<byte[]> {

    @Override
    public void close() throws IOException {
        // skip
    }

    @Override
    public byte[] get(String key) throws IOException {
        CoordinationState state = new CoordinationState();
        shards(key).forEach(shard -> state.tryRetrieve(shard, key, factor() - ack()));
        return state.anyValue();
    }

    @Override
    public void upsert(String key, byte[] value) throws IOException {
        coordinate(key, shard -> shard.upsert(key, value));
    }

    @Override
    public void delete(String key) throws IOException {
        coordinate(key, shard -> shard.delete(key));
    }

    private void coordinate(String key, CoordinationState.Operation operation) {
        CoordinationState state = new CoordinationState();
        shards(key).forEach(shard -> state.tryRun(shard, operation, factor() - ack()));
    }

    private List<Shard> shards(String key) {
        return cluster.shards(key).limit(factor()).toList();
    }

    private int factor() {
        return new ClusterDescription().length();
    }

    private int ack() {
        return new RequestAck().find(exchange);
    }

}
