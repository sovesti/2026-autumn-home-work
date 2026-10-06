package company.vk.edu.distrib.compute.sovesti.kv;

import java.io.IOException;
import java.net.http.HttpClient;

import company.vk.edu.distrib.compute.Dao;
import company.vk.edu.distrib.compute.kv.KVService;
import company.vk.edu.distrib.compute.kv.RemoteDaoFactory;
import company.vk.edu.distrib.compute.kv.RemoteDaoFactoryTest;
import company.vk.edu.distrib.compute.sovesti.urlshortener.dao.DecodingDao;

@RemoteDaoFactoryTest
public final class VfedorovRemoteDaoFactory implements RemoteDaoFactory<String> {

    @Override
    public Dao<String> create(int... ports) throws IOException {
        KVService service = new VfedorovKVServiceFactory().create(ports[0]);
        service.start();
        return new DecodingDao(new RemoteDao(service, ports[0], HttpClient.newHttpClient()));
    }

}
