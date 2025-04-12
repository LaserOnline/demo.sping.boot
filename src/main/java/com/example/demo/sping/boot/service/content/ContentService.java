package com.example.demo.sping.boot.service.content;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Base64;

import javax.imageio.ImageIO;

import org.apache.tika.Tika;
import org.springframework.stereotype.Service;

import com.example.demo.sping.boot.config.Config;
import com.example.demo.sping.boot.service.uuid.UuidService;

import net.coobird.thumbnailator.Thumbnails;

@Service
public class ContentService {
    private final Config config;
    private final Tika tika = new Tika();

    public ContentService(Config config) {
        this.config = config;
    }

    public String uploadFile(String base64) {
        try {
            byte[] bytes = decodeBase64ToBytes(base64);
            validateMimeType(bytes);
            BufferedImage image = validateAndReadImage(bytes);
            BufferedImage resizedImage = resizeImageIfNeeded(image);
            String filename = saveImage(resizedImage);
            return filename;

        } catch (IOException | IllegalArgumentException e) {
            throw new IllegalArgumentException("อัปโหลดไฟล์ล้มเหลว: " + e.getMessage(), e);
        }
    }

    protected byte[] decodeBase64ToBytes(String base64) {
        try {
            return Base64.getDecoder().decode(base64);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Base64 ไม่ถูกต้อง", e);
        }
    }

    protected void validateMimeType(byte[] bytes) {
        String mimeType = tika.detect(bytes);
        if (!mimeType.startsWith("image/")) {
            throw new IllegalArgumentException("อนุญาตเฉพาะไฟล์ภาพเท่านั้น");
        }
    }

    protected BufferedImage validateAndReadImage(byte[] bytes) {
        try {
            BufferedImage image = ImageIO.read(new ByteArrayInputStream(bytes));
            if (image == null) {
                throw new IllegalArgumentException("ไฟล์ไม่ใช่ภาพที่สามารถเปิดได้");
            }
            return image;
        } catch (IOException e) {
            throw new IllegalArgumentException("อ่านไฟล์ภาพล้มเหลว", e);
        }
    }

    protected BufferedImage resizeImageIfNeeded(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();

        if (width <= 1920 && height <= 1920) {
            return image; // ไม่ต้อง resize
        }

        double scaleRatio = Math.min(1920.0 / width, 1920.0 / height);
        int targetWidth = (int) (width * scaleRatio);
        int targetHeight = (int) (height * scaleRatio);

        try {
            return Thumbnails.of(image)
                    .size(targetWidth, targetHeight)
                    .asBufferedImage();
        } catch (IOException e) {
            throw new IllegalArgumentException("Resize ภาพล้มเหลว", e);
        }
    }

    protected String saveImage(BufferedImage image) throws IOException {
        String filename = UuidService.generateUuidWithTimestamp() + ".jpg";
        String savePath = Paths.get(config.getNginxUploadPath(), filename).toString();

        Thumbnails.of(image)
                  .size(image.getWidth(), image.getHeight())  // ใช้ขนาดปัจจุบัน
                  .outputFormat("jpg")
                  .toFile(new File(savePath));

        return filename;
    }
}
