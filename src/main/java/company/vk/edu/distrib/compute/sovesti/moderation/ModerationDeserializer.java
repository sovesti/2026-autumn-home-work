package company.vk.edu.distrib.compute.sovesti.moderation;

import java.time.Instant;
import java.util.List;

import org.apache.kafka.common.serialization.Deserializer;

import company.vk.edu.distrib.compute.moderation.ModerationResultEvent;

public final class ModerationDeserializer implements Deserializer<ModerationResultEvent> {

    @Override
    public ModerationResultEvent deserialize(String topic, byte[] data) {
        return new WithInstantDeserializer<>(3, this::event).deserialize(data).orElse(null);
    }

    private ModerationResultEvent event(List<String> parts, Instant instant) {
        return new ModerationResultEvent(parts.get(0), Boolean.valueOf(parts.get(1)), parts.get(2), instant);
    }

}
