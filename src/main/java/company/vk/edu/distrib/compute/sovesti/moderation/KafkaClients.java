package company.vk.edu.distrib.compute.sovesti.moderation;

import java.io.Closeable;

import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;

import company.vk.edu.distrib.compute.moderation.LinkCreatedEvent;
import company.vk.edu.distrib.compute.moderation.ModerationResultEvent;

record KafkaClients(KafkaProducer<String, ModerationResultEvent> moderated,
                    KafkaProducer<String, byte[]> dlt,
                    KafkaConsumer<String, LinkCreatedEvent> created) implements Closeable {

    @Override
    public void close() {
        moderated.flush();
        dlt.flush();
        moderated.close();
        dlt.close();
        created.close();
    }
}
