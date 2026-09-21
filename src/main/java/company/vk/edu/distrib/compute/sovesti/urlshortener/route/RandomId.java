package company.vk.edu.distrib.compute.sovesti.urlshortener.route;

import java.util.Random;
import java.util.function.Supplier;
import java.util.stream.Stream;

final class RandomId implements Supplier<String> {

    private final Random random = new Random();
    private final String alphanum = "qwertyuiopasdfghjklzxcvbnmQWERTYUIOPASDFGHJKLZXCVBNM1234567890";
    private final int size = 10;

    @Override
    public String get() {
        return Stream.generate(this::randomChar)
            .limit(size)
            .collect(StringBuilder::new, StringBuilder::append, StringBuilder::append)
            .toString();
    }

    String throwIfInvalid(String id) {
        if (size != id.length() || !id.chars().allMatch(this::valid)) {
            throw new IllegalArgumentException("Invalid id: %s".formatted(id));
        } else {
            return id;
        }
    }

    private boolean valid(int character) {
        return alphanum.chars().anyMatch(valid -> character == valid);
    }

    private char randomChar() {
        return alphanum.charAt(random.nextInt(alphanum.length()));
    }
}
