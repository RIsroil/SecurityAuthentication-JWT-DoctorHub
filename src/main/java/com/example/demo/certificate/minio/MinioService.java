package com.example.demo.certificate.minio;

import io.minio.*;
import io.minio.messages.Item;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class MinioService {

    private final MinioClient minioClient;

    @Value("${minio.bucket-name}")
    private String bucketName;

    public MinioService(
            @Value("${minio.endpoint}") String endpoint,
            @Value("${minio.access-key}") String accessKey,
            @Value("${minio.secret-key}") String secretKey) {
        this.minioClient = MinioClient.builder()
                .endpoint(endpoint)
                .credentials(accessKey, secretKey)
                .build();
    }

    public String getPermanentUrl(String fileName) {
        return "http://217.114.3.161:9000/" + bucketName + "/" + fileName;
    }

    public String uploadFile(MultipartFile file) throws Exception {
        String originalFilename = file.getOriginalFilename();
        String extension = "";

        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }

        String fileName = UUID.randomUUID().toString() + extension;

        try (InputStream inputStream = file.getInputStream()) {
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(fileName)
                            .stream(inputStream, file.getSize(), -1)
                            .contentType(file.getContentType())
                            .build()
            );
        }

        saveFileLocally(file, fileName); // Shu fayl nomi bilan lokalga ham saqlanadi

        return getPermanentUrl(fileName);
    }

    private void saveFileLocally(MultipartFile file, String fileName) throws IOException {
        String localPath = "/home/raximo3b/public_html/certificate_files/" + fileName;

        File directory = new File("/home/raximo3b/public_html/certificate_files/");
        if (!directory.exists()) {
            directory.mkdirs();  // Papkani yaratish
        }

        Path path = Paths.get(localPath);
        Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
    }

    public List<String> getAllCertificatePaths() throws Exception {
        List<String> certificateUrls = new ArrayList<>();
        Iterable<Result<Item>> items = minioClient.listObjects(ListObjectsArgs.builder().bucket(bucketName).build());

        for (Result<Item> item : items) {
            String fileName = item.get().objectName();
            String fileUrl = getPermanentUrl(fileName);
            certificateUrls.add(fileUrl);
        }

        return certificateUrls;
    }

    public void deleteFile(String fileName) throws Exception {
        minioClient.removeObject(
                RemoveObjectArgs.builder()
                        .bucket(bucketName)
                        .object(fileName)
                        .build()
        );
    }

    public ResponseEntity<?> uploadCertificate(MultipartFile file) {
        try {
            String fileUrl = uploadFile(file);
            return ResponseEntity.ok().body(fileUrl);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Faylni yuklashda xatolik: " + e.getMessage());
        }
    }
}