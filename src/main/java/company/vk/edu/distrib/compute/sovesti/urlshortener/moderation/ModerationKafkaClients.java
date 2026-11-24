package company.vk.edu.distrib.compute.sovesti.urlshortener.moderation;

import java.io.Closeable;

import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;

import company.vk.edu.distrib.compute.moderation.LinkCreatedEvent;
import company.vk.edu.distrib.compute.moderation.ModerationResultEvent;
import company.vk.edu.distrib.compute.sovesti.moderation.KafkaApi;
import company.vk.edu.distrib.compute.sovesti.moderation.LinkCreatedSerializer;
import company.vk.edu.distrib.compute.sovesti.moderation.ModerationDeserializer;

public record ModerationKafkaClients(KafkaConsumer<String, ModerationResultEvent> moderated,
                                     KafkaProducer<String, LinkCreatedEvent> created) implements Closeable {

    public ModerationKafkaClients(String servers) {
        this(
            new KafkaApi(servers).consumer("shortener", new ModerationDeserializer()), //
            new KafkaApi(servers).producer(new LinkCreatedSerializer()));
    }

    @Override
    public void close() {
        created.flush();
        created.close();
        moderated.close();
    }
}
