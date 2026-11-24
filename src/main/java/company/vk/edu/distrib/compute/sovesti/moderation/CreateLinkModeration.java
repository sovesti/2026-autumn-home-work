package company.vk.edu.distrib.compute.sovesti.moderation;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.List;
import java.util.function.Predicate;
import java.util.function.Supplier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

final class CreateLinkModeration implements Supplier<Predicate<URI>> {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public Predicate<URI> get() {
        List<String> restricted = restricted();
        return uri -> moderate(uri, restricted);
    }

    private boolean moderate(URI uri, List<String> restricted) {
        return restricted.stream().anyMatch(uri.toString()::contains);
    }

    private List<String> restricted() {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream()))) {
            return reader.lines().toList();
        } catch (Exception e) {
            logger.error("Failed to read banned words", e);
            return List.of();
        }
    }

    private InputStream stream() {
        return getClass().getResourceAsStream("/moderation/banned_words.txt");
    }
}
