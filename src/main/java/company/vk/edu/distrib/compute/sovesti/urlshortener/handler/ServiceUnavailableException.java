package company.vk.edu.distrib.compute.sovesti.urlshortener.handler;

public final class ServiceUnavailableException extends RuntimeException {

    private static final long serialVersionUID = 6084061095931713367L;

    public ServiceUnavailableException() {
        super("Not enough shards running");
    }
}
