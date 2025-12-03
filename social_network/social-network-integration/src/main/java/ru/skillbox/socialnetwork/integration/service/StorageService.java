package ru.skillbox.socialnetwork.integration.service;

import org.springframework.web.multipart.MultipartFile;

public interface StorageService {

    String addImage(MultipartFile multipartFile) throws Exception;

    void deleteImage(String link) throws Exception;
}
