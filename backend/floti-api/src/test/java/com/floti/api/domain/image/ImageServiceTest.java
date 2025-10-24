package com.floti.api.domain.image;

import com.floti.api.domain.image.service.ImageService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

import java.io.FileInputStream;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class ImageServiceTest {
    private final ImageService imageService = new ImageService();
    
    @Test
    @DisplayName("saveAndDeleteImage: 이미지 저장 & 삭제")
    void saveAndDeleteImage_success() throws Exception {
        MockMultipartFile multipartFile = new MockMultipartFile(
                "file",
                "test-image.png",
                "image/png",
                new FileInputStream("src/test/resources/test-image.png")
        );

        String imagePath = imageService.saveImage(multipartFile, "test");
        assertNotNull(imagePath);
        System.out.println("이미지 경로: " + imagePath);

        imageService.deleteImage(imagePath);
        System.out.println("삭제 완료");
    }
}
