package company.vk.edu.distrib.compute.sovesti.urlshortener.auth;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Objects;
import java.util.Optional;

import com.sun.net.httpserver.Request;

import company.vk.edu.distrib.compute.Dao;
import company.vk.edu.distrib.compute.sovesti.urlshortener.dao.KeyValuePair;
import company.vk.edu.distrib.compute.sovesti.urlshortener.handler.Http;

public final class BasicAuthentication implements AuthenticationScheme {

    private final String realm;
    private final Dao<String> users;

    public BasicAuthentication(String realm, Dao<String> users) {
        this.realm = Objects.requireNonNull(realm);
        this.users = Objects.requireNonNull(users);
    }

    @Override
    public Optional<AuthenticationScheme.Credentials> parse(Request request) {
        return Optional.ofNullable(request.getRequestHeaders().getFirst(Http.Header.Authorization))
            .map(this::token)
            .map(Base64.getDecoder()::decode)
            .map(bytes -> new String(bytes, StandardCharsets.UTF_8))
            .map(KeyValuePair::new)
            .filter(KeyValuePair::valid)
            .map(credentials -> () -> credentials.exists(users));
    }

    private String token(String value) {
        KeyValuePair auth = new KeyValuePair(value, ' ');
        if (!auth.valid() || !Http.Authentication.Basic.equals(auth.key())) {
            throw new AuthenticationException();
        }
        return auth.value();
    }

    @Override
    public String challenge() {
        return "%s %s=\"%s\'".formatted(Http.Authentication.Basic, Http.Authentication.Realm, realm);
    }
}
