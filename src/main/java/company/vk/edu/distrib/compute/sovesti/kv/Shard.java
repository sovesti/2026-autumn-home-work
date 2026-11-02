package company.vk.edu.distrib.compute.sovesti.kv;

import java.util.Map.Entry;

import company.vk.edu.distrib.compute.Dao;

record Shard(String key, Dao<byte[]> dao) {

    Shard(Entry<String, Dao<byte[]>> entry) {
        this(entry.getKey(), entry.getValue());
    }

}
