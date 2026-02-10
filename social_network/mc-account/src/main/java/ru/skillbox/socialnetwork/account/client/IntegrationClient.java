package ru.skillbox.socialnetwork.account.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "ms-integration")
public interface IntegrationClient {

    @GetMapping("/api/v1/storage/deleteByLink")
    ResponseEntity<Void> deletePhotoByLink(@RequestParam("link") String link,
                                           @RequestHeader("Authorization") String token);

}
