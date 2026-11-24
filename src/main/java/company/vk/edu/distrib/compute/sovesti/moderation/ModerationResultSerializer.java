package company.vk.edu.distrib.compute.sovesti.moderation;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.kafka.common.serialization.Serializer;

import company.vk.edu.distrib.compute.moderation.ModerationResultEvent;

public final class ModerationResultSerializer implements Serializer<ModerationResultEvent> {

    @Override
    public byte[] serialize(String topic, ModerationResultEvent data) {
        return Stream.of(data.id(), data.passed(), data.reason(), data.moderatedAt())
            .map(Object::toString)
            .collect(Collectors.joining("\n"))
            .getBytes();
    }

}
