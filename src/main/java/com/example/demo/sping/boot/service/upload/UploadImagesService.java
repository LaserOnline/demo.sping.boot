package com.example.demo.sping.boot.service.upload;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.imageio.ImageIO;

import org.apache.tika.Tika;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.sping.boot.config.Config;
import com.example.demo.sping.boot.service.uuid.UuidService;

import net.coobird.thumbnailator.Thumbnails;

@Service
public class UploadImagesService {
    private final Tika tika = new Tika();
    private final Config config;
    private final UuidService uuidService;

    public UploadImagesService(Config config, UuidService uuidService) {
        this.config = config;
        this.uuidService = uuidService;
    }

    
    // 1 ตรวจสอบ ไฟล์ image
    public boolean isImageFile(MultipartFile file) {
        try {
            String mimeType = tika.detect(file.getBytes());
            return mimeType.startsWith("image/");
        } catch (IOException e) {
            return false;
        }
    }

    // 2 ตรวจสอบว่า มี script หรือไม 
    public boolean isSafeImage(MultipartFile file) {
        try {
            BufferedImage image = ImageIO.read(new ByteArrayInputStream(file.getBytes()));
            return image != null;
        } catch (IOException e) {
            return false;
        }
    }

    // 3 Resize และแปลงภาพเป็น JPEG ขนาด 960*960
    public String resizeConvertAndSave(MultipartFile file, String usersUuid) {
        try {
            BufferedImage image = ImageIO.read(new ByteArrayInputStream(file.getBytes()));
            if (image == null) return null;
    
            // สร้างชื่อไฟล์ (ไม่มี .jpg)
            String filenameWithoutExtension = uuidService.generateUuidWithTimestamp();
    
            String savePath = config.getNginxUploadPath() + "/" + filenameWithoutExtension + ".jpg";
    
            // Resize และ save เป็นไฟล์ .jpg
            Thumbnails.of(image)
                    .size(960, 960)
                    .outputFormat("jpg")
                    .toFile(savePath);
    
            // return เฉพาะชื่อไฟล์ที่ไม่มี .jpg
            return filenameWithoutExtension;
        } catch (IOException e) {
            return null;
        }
    }

    // ฟังชัน แม่ ที่จะต้องใช้งาน
    public String processImage(MultipartFile file, String usersUuid) {
        if (!isImageFile(file)) {
            throw new IllegalArgumentException("File is not a valid image");
        }
    
        if (!isSafeImage(file)) {
            throw new IllegalArgumentException("File content is not safe or not a real image");
        }
    
        String filename = resizeConvertAndSave(file, usersUuid);
        if (filename == null) {
            throw new RuntimeException("Failed to process and save image");
        }
        return filename;
    }

    private  boolean isEmptyFilename(String filename) {
        return filename == null || filename.trim().isEmpty();
    }

    private Path buildImagePath(String filenameWithoutExtension) {
        String filenameWithExt = filenameWithoutExtension + ".jpg";
        return Paths.get(config.getNginxUploadPath(), filenameWithExt);
    }

    private boolean deleteFileIfExists(Path filePath) {
        try {
            if (Files.exists(filePath)) {
                Files.delete(filePath);
                return true;
            } else {
                return false;
            }
        } catch (IOException e) {
            return false;
        }
    }

    public boolean removeImagesOld(String oldFilename) {

        if (isEmptyFilename(oldFilename)) {
            return true;
        }

        Path filePath = buildImagePath(oldFilename);

        return deleteFileIfExists(filePath);
    }
    
}
