package company.vk.edu.distrib.compute.sovesti.moderation;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.kafka.common.serialization.Serializer;

import company.vk.edu.distrib.compute.moderation.LinkCreatedEvent;

public final class LinkCreatedSerializer implements Serializer<LinkCreatedEvent> {

    @Override
    public byte[] serialize(String topic, LinkCreatedEvent data) {
        return serialize(data);
    }

    public byte[] serialize(LinkCreatedEvent data) {
        return Stream.of(data.id(), data.url(), data.createdAt().toString())
            .collect(Collectors.joining("\n"))
            .getBytes();
    }

}
