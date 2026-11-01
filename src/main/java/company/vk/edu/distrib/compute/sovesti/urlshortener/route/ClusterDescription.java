package company.vk.edu.distrib.compute.sovesti.urlshortener.route;

import java.text.DecimalFormat;
import java.text.ParsePosition;
import java.util.Arrays;
import java.util.Objects;
import java.util.stream.IntStream;

public final class ClusterDescription {

    private final String raw = System.getProperty("CLUSTER", "");

    public boolean undefined() {
        return raw.isEmpty();
    }

    public int length() {
        return parts().length;
    }

    public IntStream ports() {
        return Arrays.stream(parts())
            .map(part -> new DecimalFormat().parse(part, new ParsePosition(0)))
            .filter(Objects::nonNull)
            .mapToInt(Number::intValue);
    }

    private String[] parts() {
        return raw.split(",", -1);
    }
}
