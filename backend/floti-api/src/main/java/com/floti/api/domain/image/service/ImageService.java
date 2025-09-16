package com.floti.api.domain.image.service;

import com.floti.api.error.exception.ImageStorageException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Service
public class ImageService {
    public String saveImage(MultipartFile file, String directory) {
        try {
            // 1. 파일명 생성
            String date = LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE);
            String uuid = UUID.randomUUID().toString();
            String fileName = date + "_" + uuid + ".jpg";

            Path uploadDir = Paths.get("uploads").resolve(directory);
            Files.createDirectories(uploadDir);
            Path filePath = uploadDir.resolve(fileName);

            // 2. JPG 변환 후 저장
            BufferedImage original = ImageIO.read(file.getInputStream());
            BufferedImage rgbImage = new BufferedImage(
                    original.getWidth(),
                    original.getHeight(),
                    BufferedImage.TYPE_INT_RGB
            );
            rgbImage.createGraphics().drawImage(original, 0, 0, Color.WHITE, null);
            ImageIO.write(rgbImage, "jpg", filePath.toFile());

            return directory + "/" + fileName;
        } catch (IOException e) {
            throw new ImageStorageException("이미지 저장에 실패하였습니다.");
        }
    }

    public void deleteImage(String path) {
        if (path == null) return;

        Path filePath = Paths.get("uploads").resolve(path);
        try {
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            throw new ImageStorageException("이미지 삭제에 실패하였습니다.");
        }
    }
}
