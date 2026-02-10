package ru.skillbox.socialnetwork.integration.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.minio.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.skillbox.socialnetwork.integration.config.StorageProperties;
import ru.skillbox.socialnetwork.integration.exception.StorageException;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class MinioStorageService implements StorageService {

    private final MinioClient minioClient;
    private final StorageProperties properties;
    private final ObjectMapper objectMapper;

    @Override
    public String addImage(MultipartFile file) throws Exception {
        log.info("Starting image upload process for file: {}", file.getOriginalFilename());

        if (file.isEmpty()) {
            throw new StorageException("Cannot upload empty file");
        }

        String originalFileName = file.getOriginalFilename();
        String fileName = this.generateFileName(originalFileName);

        Map<String, String> metadata = new HashMap<>();
        metadata.put("original-filename", originalFileName != null ? originalFileName : "unknown");
        metadata.put("content-type", file.getContentType());
        metadata.put("upload-timestamp", LocalDateTime.now().toString());

        PutObjectArgs putObjectArgs = PutObjectArgs.builder()
                .bucket(properties.getS3().getBucketName())
                .object(fileName)
                .stream(file.getInputStream(), file.getSize(), -1)
                .contentType(file.getContentType())
                .userMetadata(metadata)
                .build();

        minioClient.putObject(putObjectArgs);
        log.info("Image uploaded successfully. Original: {}, Stored: {}", originalFileName, fileName);

        return String.format("%s/%s/%s",
                properties.getS3().getEndpoint(),
                properties.getS3().getBucketName(),
                fileName);
    }

    @Override
    public void deleteImage(String link) throws Exception {
        log.info("Attempting to delete image: {}", link);

        if (link == null || link.trim().isEmpty()) {
            throw new StorageException("Image link cannot be null or empty");
        }

        String fileName = this.extractFileNameFromLink(link);

        RemoveObjectArgs removeObjectArgs = RemoveObjectArgs.builder()
                .bucket(properties.getS3().getBucketName())
                .object(fileName)
                .build();

        minioClient.removeObject(removeObjectArgs);
        log.info("Image deleted successfully: {}", fileName);
    }

    private String generateFileName(String fileName) {
        String uuid = UUID.randomUUID().toString();

        if (fileName == null || fileName.trim().isEmpty()) {
            return "image_" + uuid + ".jpg";
        }

        int index = fileName.lastIndexOf(".");
        String extension = index != -1 ? fileName.substring(index + 1) : ".jpg";
        String cleanName = this.getCleanName(fileName, index);

        return String.format("%s_%s%s", cleanName, uuid, extension);
    }

    private @NotNull String getCleanName(String fileName, int index) {
        String nameWithoutExtension = index != -1 ? fileName.substring(0, index) : fileName;

        String cleanName = nameWithoutExtension
                .replaceAll("\\s+", "_")
                .replaceAll("[^a-zA-Z0-9а-яА-ЯёЁ\\-_ ]", "")
                .replaceAll("_{2,}", "_")
                .replaceAll("^_+|_+$", "")
                .trim();

        if (cleanName.isEmpty()) {
            cleanName = "image";
        }

        if (cleanName.length() > 100) {
            cleanName = cleanName.substring(0, 100);
        }
        return cleanName;
    }

    private String extractFileNameFromLink(String link) {
        try {
            URI uri = new URI(link);
            String path = uri.getPath();
            int index = path.lastIndexOf("/");
            return index != -1 ? path.substring(index + 1) : path;
        } catch (Exception e) {
            throw new StorageException("Invalid file link: no file name found in path");
        }
    }

    @Scheduled(initialDelayString = "#{${storage.schedule.delay.initial-delay-seconds:5}}",
            timeUnit = TimeUnit.SECONDS)
    public void createBucket() {
        String bucketName = properties.getS3().getBucketName();
        log.debug("Checking bucket existence: {}", bucketName);
        try {
            boolean bucketExists = minioClient.bucketExists(BucketExistsArgs.builder()
                    .bucket(bucketName)
                    .build());

            if (!bucketExists) {
                log.info("Creating new bucket: {}", bucketName);
                minioClient.makeBucket(MakeBucketArgs.builder()
                        .bucket(bucketName)
                        .build());

                this.setPublicBucketPolicy(bucketName);
                log.info("Bucket created and configured successfully: {}", bucketName);
            } else {
                log.debug("Bucket already exists: {}", bucketName);
            }

        } catch (Exception e) {
            log.error("Failed to create or configure bucket: {}", bucketName, e);
            throw new StorageException("Bucket initialization failed: " + e.getMessage(), e);
        }
    }

    private void setPublicBucketPolicy(String bucketName) {
        log.debug("Setting public policy for bucket: {}", bucketName);

        try {
            ObjectNode policy = objectMapper.createObjectNode();

            policy.put("Version", "2012-10-17");

            ArrayNode statements = objectMapper.createArrayNode();
            ObjectNode statement = objectMapper.createObjectNode();
            statement.put("Effect", "Allow");
            statement.put("Principal", "*");
            statement.put("Action", "s3:GetObject");
            statement.put("Resource", "arn:aws:s3:::" + bucketName + "/*");

            statements.add(statement);
            policy.set("Statement", statements);

            String policyString = objectMapper.writeValueAsString(policy);

            minioClient.setBucketPolicy(SetBucketPolicyArgs.builder()
                    .bucket(bucketName)
                    .config(policyString)
                    .build());

            log.info("Public policy set for bucket: {}", bucketName);

        } catch (Exception e) {
            log.error("Failed to set public policy: {}", e.getMessage());
        }
    }

    public boolean imageExists(String link) {
        try {
            String fileName = extractFileNameFromLink(link);
            minioClient.statObject(StatObjectArgs.builder()
                    .bucket(properties.getS3().getBucketName())
                    .object(fileName)
                    .build());
            return true;
        } catch (Exception e) {
            log.debug("Image not found: {}", link);
            return false;
        }
    }
}
