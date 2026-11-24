package company.vk.edu.distrib.compute.sovesti.moderation;

import java.util.Map;
import java.util.Objects;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;

public final class KafkaApi {

    private final String servers;

    public KafkaApi(String servers) {
        this.servers = Objects.requireNonNull(servers);
    }

    public <T> KafkaProducer<String, T> producer(Serializer<T> serializer) {
        return new KafkaProducer<>(producerConfig(), new StringSerializer(), serializer);
    }

    public <T> KafkaConsumer<String, T> consumer(String group, Deserializer<T> deserializer) {
        return new KafkaConsumer<>(consumerConfig(group), new StringDeserializer(), deserializer);
    }

    private Map<String, Object> producerConfig() {
        return Map.of(
            ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, servers, ProducerConfig.MAX_BLOCK_MS_CONFIG, 100);
    }

    private Map<String, Object> consumerConfig(String group) {
        return Map.of(
            ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, servers, //
            ConsumerConfig.GROUP_ID_CONFIG, group, //
            ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
    }
}
