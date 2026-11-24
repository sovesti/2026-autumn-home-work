package company.vk.edu.distrib.compute.sovesti.urlshortener.moderation;

import java.time.Duration;
import java.util.Objects;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import company.vk.edu.distrib.compute.Dao;
import company.vk.edu.distrib.compute.moderation.ModerationResultEvent;

public final class PollModerated implements Runnable {

    private final ModerationKafkaClients kafka;
    private final Dao<String> links;
    private final Logger logger = LoggerFactory.getLogger(getClass());

    public PollModerated(ModerationKafkaClients kafka, Dao<String> links) {
        this.kafka = Objects.requireNonNull(kafka);
        this.links = Objects.requireNonNull(links);
    }

    @Override
    public void run() {
        while (true) {
            kafka.moderated().poll(Duration.ofMillis(5)).forEach(this::consume);
        }
    }

    private void consume(ConsumerRecord<String, ModerationResultEvent> record) {
        if (!record.value().passed()) {
            try {
                links.delete(record.key());
            } catch (Exception e) {
                logger.error("Failed to delete banned link", e);
            }
        }
    }

}
