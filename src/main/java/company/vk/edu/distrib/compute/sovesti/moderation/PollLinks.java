package company.vk.edu.distrib.compute.sovesti.moderation;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.Objects;
import java.util.function.Predicate;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import company.vk.edu.distrib.compute.moderation.LinkCreatedEvent;
import company.vk.edu.distrib.compute.moderation.ModerationResultEvent;

final class PollLinks implements Runnable {

    private final KafkaClients kafka;
    private final Predicate<URI> ban;
    private final Logger logger = LoggerFactory.getLogger(getClass());

    PollLinks(KafkaClients kafka, Predicate<URI> ban) {
        this.kafka = Objects.requireNonNull(kafka);
        this.ban = Objects.requireNonNull(ban);
    }

    @Override
    public void run() {
        while (true) {
            kafka.created().poll(Duration.ofMillis(5)).forEach(this::consume);
        }
    }

    private void consume(ConsumerRecord<String, LinkCreatedEvent> record) {
        try {
            kafka.moderated()
                .send(new ProducerRecord<>(ModerationConstants.MODERATED_TOPIC, record.key(), moderate(record)));
        } catch (Exception e) {
            recordError(record.key(), String.valueOf(record.value()), e);
        }
    }

    private void recordError(String key, String value, Exception exception) {
        logger.error("Incorrect URL: %s".formatted(value), exception);
        new DeadLetter(kafka.dlt()).send(key, value.getBytes(), exception.getMessage());
    }

    private ModerationResultEvent moderate(ConsumerRecord<String, LinkCreatedEvent> record) throws URISyntaxException {
        return moderate(record.key(), uri(record), Instant.now());
    }

    private URI uri(ConsumerRecord<String, LinkCreatedEvent> record) throws URISyntaxException {
        return new URI(URLDecoder.decode(record.value().url(), StandardCharsets.UTF_8));
    }

    private ModerationResultEvent moderate(String key, URI uri, Instant timestamp) {
        if (ban.test(uri)) {
            return new ModerationResultEvent(key, false, "URL contains forbidden word", timestamp);
        } else {
            return new ModerationResultEvent(key, true, "OK", timestamp);
        }
    }

}
