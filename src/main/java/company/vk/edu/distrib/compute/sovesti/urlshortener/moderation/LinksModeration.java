package company.vk.edu.distrib.compute.sovesti.urlshortener.moderation;

import java.io.Closeable;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import company.vk.edu.distrib.compute.Dao;
import company.vk.edu.distrib.compute.moderation.LinkCreatedEvent;
import company.vk.edu.distrib.compute.sovesti.moderation.ModerationConstants;

public final class LinksModeration implements Closeable {

    private final ModerationKafkaClients kafka;
    private final ScheduledExecutorService executor;
    private final Logger logger = LoggerFactory.getLogger(getClass());

    public LinksModeration(String servers) {
        this.kafka = new ModerationKafkaClients(servers);
        this.executor = Executors.newSingleThreadScheduledExecutor();
    }

    public void start(Dao<String> links) {
        kafka.moderated().subscribe(List.of(ModerationConstants.MODERATED_TOPIC));
        executor.submit(new PollModerated(kafka, links));
    }

    public void linkCreated(String id, String url) {
        try {
            kafka.created().send(new ProducerRecord<>(ModerationConstants.CREATED_TOPIC, id, event(id, url)));
        } catch (Exception e) {
            logger.error("Moderation error", e);
        }
    }

    private LinkCreatedEvent event(String id, String url) {
        return new LinkCreatedEvent(id, url, Instant.now());
    }

    @Override
    public void close() {
        executor.shutdownNow();
        kafka.close();
    }
}
