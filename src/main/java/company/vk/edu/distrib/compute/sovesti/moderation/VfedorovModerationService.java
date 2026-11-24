package company.vk.edu.distrib.compute.sovesti.moderation;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.common.serialization.ByteArraySerializer;

import company.vk.edu.distrib.compute.moderation.ModerationService;

public final class VfedorovModerationService implements ModerationService {

    private final KafkaClients kafka;
    private final ScheduledExecutorService executor;

    public VfedorovModerationService(String servers) {
        this.kafka = kafka(servers);
        this.executor = Executors.newSingleThreadScheduledExecutor();
    }

    private KafkaClients kafka(String servers) {
        KafkaApi api = new KafkaApi(servers);
        KafkaProducer<String, byte[]> dlt = api.producer(new ByteArraySerializer());
        return new KafkaClients(api.producer(new ModerationResultSerializer()), //
            dlt, //
            api.consumer("moderation", new LinkCreatedDeserializer(dlt)));
    }

    @Override
    public void start() {
        kafka.created().subscribe(List.of(ModerationConstants.CREATED_TOPIC));
        executor.submit(new PollLinks(kafka, new CreateLinkModeration().get()));
    }

    @Override
    public void stop() {
        executor.shutdownNow();
        kafka.close();
    }

}
