package ru.skillbox.socialnetwork.friend.common.client;

public interface AuthServiceClient {
    Boolean validateToken(String token);
}
