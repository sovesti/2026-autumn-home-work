package company.vk.edu.distrib.compute.sovesti.kv;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.util.NoSuchElementException;
import java.util.Objects;

import company.vk.edu.distrib.compute.Dao;
import company.vk.edu.distrib.compute.kv.KVService;
import company.vk.edu.distrib.compute.sovesti.urlshortener.http.StatusCodeConstants;

record RemoteDao(KVService delegate, int port, HttpClient client) implements Dao<byte[]> {

    RemoteDao {
        Objects.requireNonNull(delegate);
        Objects.requireNonNull(port);
        Objects.requireNonNull(client);
    }

    @Override
    public void close() throws IOException {
        client.close();
        delegate.stop();
    }

    @Override
    public byte[] get(String key) throws NoSuchElementException, IllegalArgumentException, IOException {
        return responseBody(key, send(key, request(key).GET().build()));
    }

    @Override
    public void upsert(String key, byte[] value) throws IllegalArgumentException, IOException {
        send(key, request(key).PUT(BodyPublishers.ofByteArray(value)).build());
    }

    @Override
    public void delete(String key) throws IllegalArgumentException, IOException {
        send(key, request(key).DELETE().build());
    }

    private HttpResponse<byte[]> send(String key, HttpRequest request) throws IOException {
        try {
            return client.send(request, HttpResponse.BodyHandlers.ofByteArray());
        } catch (InterruptedException e) {
            throw new IOException(failed(key), e);
        }
    }

    private byte[] responseBody(String key, HttpResponse<byte[]> response) throws IOException {
        return switch (response.statusCode()) {
            case StatusCodeConstants.OK -> response.body();
            case StatusCodeConstants.NOT_FOUND -> throw new NoSuchElementException(failed(key));
            default -> throw new IOException(failed(key));
        };
    }

    private String failed(String key) {
        return "Failed to send request to %s".formatted(uri(key));
    }

    private HttpRequest.Builder request(String key) {
        return HttpRequest.newBuilder(uri(key));
    }

    private URI uri(String key) {
        return URI.create("http://localhost:%d/v0/entity?id=%s".formatted(port, key));
    }

}
