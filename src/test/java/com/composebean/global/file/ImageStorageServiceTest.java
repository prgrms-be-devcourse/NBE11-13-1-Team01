package com.composebean.global.file;

import com.composebean.global.exception.BusinessException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.mock.web.MockMultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ImageStorageServiceTest {

    @TempDir
    Path tempDir;

    @Test
    @DisplayName("이미지 파일을 지정된 경로에 저장한다")
    void storeImage() {
        ImageStorageService imageStorageService =
                new ImageStorageService(tempDir.toString());

        MockMultipartFile imageFile = new MockMultipartFile(
                "imageFile",
                "coffee.png",
                "image/png",
                "image-content".getBytes()
        );

        String imageUrl = imageStorageService.store(imageFile);

        assertThat(imageUrl)
                .startsWith("/uploads/products/")
                .endsWith(".png");

        String storedFilename = imageUrl.substring(
                "/uploads/products/".length()
        );

        Path storedFile = tempDir.resolve(storedFilename);

        assertThat(Files.exists(storedFile)).isTrue();
        assertThat(storedFile).hasBinaryContent(
                "image-content".getBytes()
        );
    }

    @Test
    @DisplayName("동일한 이름의 이미지도 서로 다른 파일명으로 저장한다")
    void storeImagesWithUniqueFilename() {
        ImageStorageService imageStorageService =
                new ImageStorageService(tempDir.toString());

        MockMultipartFile firstImage = new MockMultipartFile(
                "imageFile",
                "coffee.png",
                "image/png",
                "first-image".getBytes()
        );

        MockMultipartFile secondImage = new MockMultipartFile(
                "imageFile",
                "coffee.png",
                "image/png",
                "second-image".getBytes()
        );

        String firstImageUrl =
                imageStorageService.store(firstImage);

        String secondImageUrl =
                imageStorageService.store(secondImage);

        assertThat(firstImageUrl)
                .isNotEqualTo(secondImageUrl);
    }

    @Test
    @DisplayName("빈 이미지 파일을 저장하면 예외가 발생한다")
    void storeEmptyImage() {
        ImageStorageService imageStorageService =
                new ImageStorageService(tempDir.toString());

        MockMultipartFile emptyImage = new MockMultipartFile(
                "imageFile",
                "empty.png",
                "image/png",
                new byte[0]
        );

        assertThatThrownBy(
                () -> imageStorageService.store(emptyImage)
        ).isInstanceOf(BusinessException.class);
    }

    @Test
    @DisplayName("이미지가 아닌 파일을 저장하면 예외가 발생한다")
    void storeInvalidContentType() {
        ImageStorageService imageStorageService =
                new ImageStorageService(tempDir.toString());

        MockMultipartFile textFile = new MockMultipartFile(
                "imageFile",
                "document.txt",
                "text/plain",
                "text-content".getBytes()
        );

        assertThatThrownBy(
                () -> imageStorageService.store(textFile)
        ).isInstanceOf(BusinessException.class);
    }

    @Test
    @DisplayName("Content-Type이 없는 파일을 저장하면 예외가 발생한다")
    void storeFileWithoutContentType() {
        ImageStorageService imageStorageService =
                new ImageStorageService(tempDir.toString());

        MockMultipartFile file = new MockMultipartFile(
                "imageFile",
                "coffee.png",
                null,
                "image-content".getBytes()
        );

        assertThatThrownBy(
                () -> imageStorageService.store(file)
        ).isInstanceOf(BusinessException.class);
    }

    @Test
    @DisplayName("저장된 이미지 파일을 삭제한다")
    void deleteImage() {
        ImageStorageService imageStorageService =
                new ImageStorageService(tempDir.toString());

        MockMultipartFile imageFile = new MockMultipartFile(
                "imageFile",
                "coffee.png",
                "image/png",
                "image-content".getBytes()
        );

        String imageUrl =
                imageStorageService.store(imageFile);

        String storedFilename = imageUrl.substring(
                "/uploads/products/".length()
        );

        Path storedFile = tempDir.resolve(storedFilename);

        imageStorageService.delete(imageUrl);

        assertThat(Files.exists(storedFile)).isFalse();
    }

    @Test
    @DisplayName("존재하지 않는 이미지 파일을 삭제해도 예외가 발생하지 않는다")
    void deleteMissingImage() {
        ImageStorageService imageStorageService =
                new ImageStorageService(tempDir.toString());

        imageStorageService.delete(
                "/uploads/products/missing-image.png"
        );
    }

    @Test
    @DisplayName("업로드 경로가 아닌 이미지 URL은 삭제하지 않는다")
    void ignoreExternalImageUrl() {
        ImageStorageService imageStorageService =
                new ImageStorageService(tempDir.toString());

        imageStorageService.delete(
                "https://example.com/image.png"
        );
    }
}