package company.vk.edu.distrib.compute.sovesti.kv;

import java.io.IOException;

import company.vk.edu.distrib.compute.AbstractHttpServiceFactory;
import company.vk.edu.distrib.compute.kv.KVServiceTest;
import company.vk.edu.distrib.compute.kv.ReplicatedKVServiceTest;
import company.vk.edu.distrib.compute.sovesti.urlshortener.handler.HttpServers;

@KVServiceTest
@ReplicatedKVServiceTest
public final class VfedorovKVServiceFactory extends AbstractHttpServiceFactory<VfedorovKVService> {

    @Override
    protected VfedorovKVService doCreate(int port) throws IOException {
        return new VfedorovKVService(port, new HttpServers().create(port));
    }

}
