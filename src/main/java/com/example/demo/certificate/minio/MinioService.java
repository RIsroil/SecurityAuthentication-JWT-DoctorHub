package com.example.demo.certificate.minio;

import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import io.minio.ListObjectsArgs;
import io.minio.Result;
import io.minio.errors.MinioException;
import io.minio.messages.Item;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
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

@Slf4j
@Service
@RequiredArgsConstructor
public class MinioService {

    private final MinioClient minioClient;
    private final MinioConfig minioConfig;

    @Value("${minio.bucket-name}")
    private String bucketName;

    public String getPermanentUrl(String fileName) {
        String endpoint = minioConfig.getEndpoint();
        if (endpoint.endsWith("/")) {
            endpoint = endpoint.substring(0, endpoint.length() - 1);
        }
        return endpoint + "/" + bucketName + "/" + fileName;
    }

    public String uploadFile(MultipartFile file) throws MinioException, IOException {
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

        // REVIEW: The following line saves a copy of the file to the local filesystem.
        // See the comment on the saveFileLocally method itself for implications and
        // whether this is strictly necessary when using MinIO.
        saveFileLocally(file, fileName); // Shu fayl nomi bilan lokalga ham saqlanadi

        return getPermanentUrl(fileName);
    }

    /**
     * Saves a copy of the uploaded file to the local filesystem.
     * NOTE: This behavior is unusual when using an object storage service like MinIO,
     * as MinIO is itself a persistent storage solution.
     * Consider the following:
     * - Is this local save strictly necessary for the application's workflow?
     * - This creates a dependency on the local filesystem, which can be problematic for scalability (e.g., if running multiple instances of the application).
     * - Ensure the directory (/home/raximo3b/public_html/certificate_files/) has adequate permissions and disk space.
     * - If this is for backup or an alternative serving mechanism, ensure this is documented and understood.
     * - If MinIO is the primary reliable store, this local copy might be redundant and could be removed
     *   to simplify the system and reduce local storage dependencies.
     * Please review if this local saving is essential.
     *
     * @param file the multipart file to save
     * @param fileName the name to save the file as
     * @throws IOException if an I/O error occurs
     */
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
        } catch (MinioException e) {
            // Log the exception for server-side details
            log.error("MinIO error during file upload: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error during file upload to storage: " + e.getMessage());
        } catch (IOException e) {
            log.error("IO error during file upload: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error processing file: " + e.getMessage());
        } catch (Exception e) {
            // Fallback for any other unexpected exception from uploadFile, though ideally it should only declare specific ones.
            log.error("Unexpected error during file upload: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred during file upload.");
        }
    }
}