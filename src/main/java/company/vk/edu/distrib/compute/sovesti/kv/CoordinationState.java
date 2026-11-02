package company.vk.edu.distrib.compute.sovesti.kv;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import company.vk.edu.distrib.compute.Dao;
import company.vk.edu.distrib.compute.sovesti.urlshortener.handler.ServiceUnavailableException;

final class CoordinationState {
    private final Logger log = LoggerFactory.getLogger(getClass());
    private int failures;
    private final List<byte[]> values = new ArrayList<>();

    void tryRun(Shard shard, Operation operation, int threshold) {
        try {
            operation.run(shard.dao());
        } catch (Exception e) {
            failure(threshold);
        }
    }

    void tryRetrieve(Shard shard, String key, int threshold) {
        try {
            values.add(shard.dao().get(key));
        } catch (NoSuchElementException e) {
            logNotFound(shard, key);
        } catch (Exception e) {
            failure(threshold);
        }
    }

    private void logNotFound(Shard shard, String key) {
        if (log.isDebugEnabled()) {
            log.debug("{} not found on {}", key, shard.key());
        }
    }

    private void failure(int threshold) {
        failures++;
        if (failures > threshold) {
            throw new ServiceUnavailableException();
        }
    }

    byte[] anyValue() {
        return values.getFirst();
    }

    @FunctionalInterface
    interface Operation {
        void run(Dao<byte[]> shard) throws IOException;
    }
}
