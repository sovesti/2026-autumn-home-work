package company.vk.edu.distrib.compute.sovesti.urlshortener.handler;

public final class UnknownPathException extends RuntimeException {

    private static final long serialVersionUID = 1235942363641479104L;

    public UnknownPathException(String path) {
        super("Unknown path: %s".formatted(path));
    }
}
