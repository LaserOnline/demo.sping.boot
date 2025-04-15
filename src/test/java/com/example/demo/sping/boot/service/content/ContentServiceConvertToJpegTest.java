package com.example.demo.sping.boot.service.content;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.List;

import javax.imageio.ImageIO;

import org.apache.tika.Tika;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

import com.example.demo.sping.boot.config.Config;
public class ContentServiceConvertToJpegTest {

    // step 4
    private final ContentService contentService = new ContentService(new Config());
    private final Tika tika = new Tika();

    @Test
    void validateAndConvertToJpeg_shouldConvertPngToJpeg() throws Exception {
        // สร้าง PNG จาก BufferedImage
        BufferedImage bufferedImage = new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(bufferedImage, "png", baos);
        byte[] pngBytes = baos.toByteArray();

        // ตรวจว่าเป็น PNG จริง ๆ
        assertEquals("image/png", tika.detect(pngBytes));

        // แปลง
        List<byte[]> result = contentService.validateAndConvertToJpeg(List.of(pngBytes));
        assertEquals(1, result.size());

        byte[] jpegResult = result.get(0);

        // ✅ ตรวจว่าไฟล์ที่ได้เป็น image/jpeg
        String detectedMime = tika.detect(jpegResult);
        System.out.println("🔍 MIME after conversion: " + detectedMime);
        assertEquals("image/jpeg", detectedMime);
    }

    @Test
    void validateAndConvertToJpeg_shouldSkipIfAlreadyJpeg() throws Exception {
        // สร้าง JPEG จาก BufferedImage
        BufferedImage bufferedImage = new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(bufferedImage, "jpg", baos);
        byte[] jpegBytes = baos.toByteArray();

        // ตรวจว่าเป็น JPEG จริง ๆ
        assertEquals("image/jpeg", tika.detect(jpegBytes));

        // ส่งเข้าไปแปลง
        List<byte[]> result = contentService.validateAndConvertToJpeg(List.of(jpegBytes));
        assertEquals(1, result.size());

        byte[] output = result.get(0);

        // ✅ ไม่ควรเปลี่ยน MIME type
        assertEquals("image/jpeg", tika.detect(output));
    }
    // step 4
}