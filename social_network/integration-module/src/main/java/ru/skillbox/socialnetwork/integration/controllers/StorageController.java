package ru.skillbox.socialnetwork.integration.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.skillbox.socialnetwork.integration.config.StorageProperties;
import ru.skillbox.socialnetwork.integration.dto.image.ImageFileDto;
import ru.skillbox.socialnetwork.integration.service.StorageService;

import java.io.IOException;

@Slf4j
@RestController
@RequestMapping("/api/v1/storage")
@RequiredArgsConstructor
public class StorageController {

    private final StorageService storageService;
    private final StorageProperties properties;

    @PostMapping("/storageUserImage")
    public ImageFileDto addUserImage(@RequestParam("file") MultipartFile file) {
        log.info("Received file upload request: {}", file.getOriginalFilename());

        try {
            String fileUrl = storageService.addImage(file);

            return new ImageFileDto(fileUrl);

        } catch (IllegalArgumentException e) {
            log.error("Storage error: {}", e.getMessage(), e);
            throw new IllegalArgumentException();
        } catch (IOException e) {
            log.error("Unexpected error: {}", e.getMessage(), e);
            throw new RuntimeException(e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @GetMapping("/deleteByLink")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUserImageByLink(@RequestParam String link) {
        try {
            log.info("Received delete request for link: {}", link);
            storageService.deleteImage(link);
        } catch (IllegalArgumentException e) {
            log.error("Error deleting image: {}", e.getMessage());
            throw new IllegalArgumentException("Failed to delete image: " + e.getMessage());
        } catch (Exception e) {
            log.error("Unexpected error during deletion: {}", e.getMessage());
            throw new IllegalArgumentException("Internal server error");
        }
    }
}
