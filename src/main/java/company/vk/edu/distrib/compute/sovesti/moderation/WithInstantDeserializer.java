package company.vk.edu.distrib.compute.sovesti.moderation;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiFunction;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class WithInstantDeserializer<T> {

    private final int parts;
    private final BiFunction<List<String>, Instant, T> create;
    private final Logger logger = LoggerFactory.getLogger(getClass());

    public WithInstantDeserializer(int parts, BiFunction<List<String>, Instant, T> create) {
        this.parts = parts;
        this.create = Objects.requireNonNull(create);
    }

    public Optional<T> deserialize(byte[] data) {
        return Optional.ofNullable(data).flatMap(this::split).flatMap(this::deserialize);
    }

    private Optional<T> deserialize(List<String> raw) {
        try {
            return Optional.of(create.apply(raw, Instant.parse(raw.getLast())));
        } catch (DateTimeParseException e) {
            logger.error("Failed to parse link creation date", e);
            return Optional.empty();
        }
    }

    private Optional<List<String>> split(byte[] data) {
        List<String> raw = Arrays.asList(new String(data, StandardCharsets.UTF_8).split("\n"));
        if (raw.size() != parts + 1) {
            return Optional.empty();
        }
        return Optional.of(raw);
    }
}
