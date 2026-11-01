package company.vk.edu.distrib.compute.sovesti.kv;

import java.net.http.HttpClient;
import java.util.function.Function;

import com.sun.net.httpserver.HttpExchange;

import company.vk.edu.distrib.compute.Dao;
import company.vk.edu.distrib.compute.sovesti.urlshortener.handler.ExchangeAttribute.QueryAttribute;
import company.vk.edu.distrib.compute.sovesti.urlshortener.handler.ExchangeAttributes;
import company.vk.edu.distrib.compute.sovesti.urlshortener.route.ClusterDescription;

record ReplicatingClusterDao(String key, int port, Dao<byte[]> local,
                             HttpClient client) implements Function<HttpExchange, Dao<byte[]>> {

    @Override
    public Dao<byte[]> apply(HttpExchange exchange) {
        if (new ClusterDescription().undefined() || alreadyCoordinated(exchange)) {
            return local;
        }
        return new CoordinatedClusterDao(withShards(new ClusterDao()), exchange);
    }

    private boolean alreadyCoordinated(HttpExchange exchange) {
        return new ExchangeAttributes(exchange).find(new QueryAttribute())
            .flatMap(query -> query.get("coordinated"))
            .isPresent();
    }

    private ClusterDao withShards(ClusterDao cluster) {
        ensureLocalInCluster();
        cluster.putShard(String.valueOf(port), local);
        new ClusterDescription().ports()
            .filter(other -> other != port)
            .forEach(port -> cluster.putShard(String.valueOf(port), new RemoteDao(port, client)));
        return cluster;
    }

    private void ensureLocalInCluster() {
        if (new ClusterDescription().ports().noneMatch(node -> node == port)) {
            throw new IllegalStateException("No %d node in cluster".formatted(port));
        }
    }
}
