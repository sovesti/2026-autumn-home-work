package company.vk.edu.distrib.compute.sovesti.kv;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.net.httpserver.HttpExchange;

import company.vk.edu.distrib.compute.Dao;
import company.vk.edu.distrib.compute.sovesti.urlshortener.handler.ServiceUnavailableException;
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

    private void coordinate(String key, Operation operation) {
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

    private final class CoordinationState {
        private final Logger log = LoggerFactory.getLogger(getClass());
        private int failures;
        private final List<byte[]> values = new ArrayList<>();

        private void tryRun(Shard shard, Operation operation, int threshold) {
            try {
                operation.run(shard.dao());
            } catch (Exception e) {
                failure(threshold);
            }
        }

        private void tryRetrieve(Shard shard, String key, int threshold) {
            try {
                values.add(shard.dao().get(key));
            } catch (NoSuchElementException e) {
                if (log.isDebugEnabled()) {
                    log.debug("{} not found on {}", key, shard.key());
                }
            } catch (Exception e) {
                failure(threshold);
            }
        }

        private void failure(int threshold) {
            failures++;
            if (failures > threshold) {
                throw new ServiceUnavailableException();
            }
        }

        private byte[] anyValue() {
            return values.getFirst();
        }
    }

    @FunctionalInterface
    private interface Operation {
        void run(Dao<byte[]> shard) throws IOException;
    }
}
