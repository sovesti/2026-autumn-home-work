package company.vk.edu.distrib.compute.sovesti.moderation;

import java.util.Objects;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;

final class DeadLetter {

    private final KafkaProducer<String, byte[]> dlt;

    public DeadLetter(KafkaProducer<String, byte[]> dlt) {
        this.dlt = Objects.requireNonNull(dlt);
    }

    public void send(String key, byte[] value, String error) {
        ProducerRecord<String, byte[]> message = new ProducerRecord<>(ModerationConstants.DLT_TOPIC, key, value);
        message.headers().add(ModerationConstants.ERROR_HEADER, errorHeader(error));
        dlt.send(message);
    }

    private byte[] errorHeader(String error) {
        return "Failed to parse link: %s".formatted(error).getBytes();
    }
}
