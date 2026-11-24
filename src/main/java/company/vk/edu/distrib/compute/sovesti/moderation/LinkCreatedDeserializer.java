package company.vk.edu.distrib.compute.sovesti.moderation;

import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.common.serialization.Deserializer;

import company.vk.edu.distrib.compute.moderation.LinkCreatedEvent;

public final class LinkCreatedDeserializer implements Deserializer<LinkCreatedEvent> {

    private final KafkaProducer<String, byte[]> dlt;

    public LinkCreatedDeserializer(KafkaProducer<String, byte[]> dlt) {
        this.dlt = Objects.requireNonNull(dlt);
    }

    @Override
    public LinkCreatedEvent deserialize(String topic, byte[] data) {
        return new WithInstantDeserializer<>(2, this::event).deserialize(data).or(() -> noEvent(data)).orElse(null);
    }

    private Optional<LinkCreatedEvent> noEvent(byte[] data) {
        new DeadLetter(dlt).send(UUID.randomUUID().toString(), data, "Invalid message");
        return Optional.empty();
    }

    private LinkCreatedEvent event(List<String> parts, Instant instant) {
        return new LinkCreatedEvent(parts.get(0), parts.get(1), instant);
    }

}
