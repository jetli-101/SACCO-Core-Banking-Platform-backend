package com.example.sacco_core_banking.services;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

import com.example.sacco_core_banking.classes.InvalidStateException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/**
 * Local-disk storage for user-uploaded images (profile photos). Files are written under
 * {app.upload.dir}/{subdir}/ and served back via WebConfig's /uploads/** resource handler,
 * so the URL returned here is what gets persisted on the owning entity.
 */
@Service
public class FileStorageService {

    private static final Logger logger = LoggerFactory.getLogger(FileStorageService.class);
    private static final long MAX_FILE_SIZE_BYTES = 5L * 1024 * 1024;

    @Value("${app.upload.dir}")
    private String uploadDir;

    public String store(MultipartFile file, String subdir, UUID ownerId) {
        if (file == null || file.isEmpty()) {
            throw new InvalidStateException("No file was uploaded");
        }
        if (file.getSize() > MAX_FILE_SIZE_BYTES) {
            throw new InvalidStateException("File is too large — maximum size is 5MB");
        }
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new InvalidStateException("Only image files are allowed");
        }

        String extension = "";
        String originalName = file.getOriginalFilename();
        if (originalName != null && originalName.contains(".")) {
            extension = originalName.substring(originalName.lastIndexOf('.'));
        }

        try {
            Path targetDir = Paths.get(uploadDir, subdir);
            Files.createDirectories(targetDir);

            String filename = ownerId + "-" + UUID.randomUUID() + extension;
            Path targetFile = targetDir.resolve(filename);

            try (InputStream in = file.getInputStream()) {
                Files.copy(in, targetFile, StandardCopyOption.REPLACE_EXISTING);
            }

            return "/uploads/" + subdir + "/" + filename;
        } catch (IOException e) {
            throw new InvalidStateException("Unable to store the uploaded file");
        }
    }

    /** Best-effort cleanup of a previously stored file — swallows failures, never blocks the caller. */
    public void delete(String url) {
        if (url == null || !url.startsWith("/uploads/")) return;
        try {
            Path path = Paths.get(uploadDir, url.substring("/uploads/".length()));
            Files.deleteIfExists(path);
        } catch (IOException e) {
            logger.warn("Failed to delete stored file {}", url, e);
        }
    }
}
