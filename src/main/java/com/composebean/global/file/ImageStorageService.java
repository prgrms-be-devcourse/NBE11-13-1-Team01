package com.composebean.global.file;

import com.composebean.global.exception.BusinessException;
import com.composebean.global.exception.ErrorCode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Set;
import java.util.UUID;

@Service
public class ImageStorageService {

    private static final Set<String> ALLOWED_CONTENT_TYPES = Set.of(
            "image/jpeg",
            "image/png",
            "image/webp",
            "image/gif"
    );

    private final Path uploadPath;

    public ImageStorageService(
            @Value("${file.upload-dir:uploads/products}")
            String uploadDir
    ) {
        this.uploadPath = Paths.get(uploadDir)
                .toAbsolutePath()
                .normalize();

        createUploadDirectory();
    }

    public String store(MultipartFile imageFile) {
        validateImage(imageFile);

        String originalFilename = StringUtils.cleanPath(
                imageFile.getOriginalFilename() == null
                        ? "image"
                        : imageFile.getOriginalFilename()
        );

        String extension = extractExtension(originalFilename);
        String storedFilename = UUID.randomUUID() + extension;

        Path targetPath = uploadPath.resolve(storedFilename)
                .normalize();

        validateTargetPath(targetPath);

        try {
            Files.copy(
                    imageFile.getInputStream(),
                    targetPath,
                    StandardCopyOption.REPLACE_EXISTING
            );
        } catch (IOException exception) {
            throw new BusinessException(
                    ErrorCode.IMAGE_STORAGE_FAILED
            );
        }

        return "/uploads/products/" + storedFilename;
    }

    private void validateImage(MultipartFile imageFile) {
        if (imageFile == null || imageFile.isEmpty()) {
            throw new BusinessException(
                    ErrorCode.INVALID_IMAGE_FILE
            );
        }

        String contentType = imageFile.getContentType();

        if (contentType == null
                || !ALLOWED_CONTENT_TYPES.contains(contentType)) {
            throw new BusinessException(
                    ErrorCode.INVALID_IMAGE_FILE
            );
        }
    }

    private String extractExtension(String filename) {
        int extensionIndex = filename.lastIndexOf('.');

        if (extensionIndex == -1) {
            return "";
        }

        return filename.substring(extensionIndex).toLowerCase();
    }

    private void validateTargetPath(Path targetPath) {
        if (!targetPath.startsWith(uploadPath)) {
            throw new BusinessException(
                    ErrorCode.INVALID_IMAGE_FILE
            );
        }
    }

    private void createUploadDirectory() {
        try {
            Files.createDirectories(uploadPath);
        } catch (IOException exception) {
            throw new BusinessException(
                    ErrorCode.IMAGE_STORAGE_FAILED
            );
        }
    }

    public void delete(String imageUrl) {
        if (imageUrl == null || imageUrl.isBlank()) {
            return;
        }

        String prefix = "/uploads/products/";

        if (!imageUrl.startsWith(prefix)) {
            return;
        }

        String filename = imageUrl.substring(prefix.length());

        if (filename.isBlank()) {
            return;
        }

        Path targetPath = uploadPath.resolve(filename)
                .normalize();

        validateTargetPath(targetPath);

        try {
            Files.deleteIfExists(targetPath);
        } catch (IOException exception) {
            throw new BusinessException(
                    ErrorCode.IMAGE_STORAGE_FAILED
            );
        }
    }
}