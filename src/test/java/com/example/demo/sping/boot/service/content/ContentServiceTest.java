package com.example.demo.sping.boot.service.content;

import java.util.Base64;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.example.demo.sping.boot.config.Config;

public class ContentServiceTest {
    private ContentService contentService;

    @BeforeEach
    void setUp() {
        Config mockConfig = new Config();
        contentService = new ContentService(mockConfig);
    }

    // step 1
    @Test
    void validateBase64Strings_shouldPass_withValidBase64() {
        String validBase64 = Base64.getEncoder().encodeToString("hello".getBytes());
        assertDoesNotThrow(() ->
            contentService.validateBase64Strings(List.of(validBase64))
        );
    }

    
    @Test
    void validateBase64Strings_shouldThrow_whenInvalidBase64() {
        List<String> base64List = List.of("not-base64###", "%%%invalid%%%");
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> contentService.validateBase64Strings(base64List)
        );
        assertTrue(exception.getMessage().contains("base64 incorrect"));
    }
    // step 1

    // step 2
    @Test
    void decodeAllBase64_shouldDecodeSuccessfully() {
        // เตรียมข้อมูล base64 ของคำว่า "hello"
        String base64 = Base64.getEncoder().encodeToString("hello".getBytes());

        List<byte[]> decoded = contentService.decodeAllBase64(List.of(base64));

        assertEquals(1, decoded.size());
        String decodedString = new String(decoded.get(0));
        assertEquals("hello", decodedString);
    }
    // step 2

    // step 3   
    @Test
    void validateAllMimeTypes_shouldPass_whenAllAreImages() {
        // ข้อมูลจำลองเป็น byte[] ของรูปภาพขนาดเล็กจริง ๆ (เช่น JPEG header)
        byte[] fakeJpeg = new byte[] {(byte)0xFF, (byte)0xD8, (byte)0xFF, (byte)0xE0}; // JPEG header

        assertDoesNotThrow(() -> contentService.validateAllMimeTypes(List.of(fakeJpeg)));
    }

    @Test
    void validateAllMimeTypes_shouldThrow_whenFileIsNotImage() {
        // จำลองข้อมูล text file
        byte[] textFileBytes = "This is plain text.".getBytes();

        IllegalArgumentException ex = assertThrows(
            IllegalArgumentException.class,
            () -> contentService.validateAllMimeTypes(List.of(textFileBytes))
        );

        assertTrue(ex.getMessage().contains("is not images"));
    }
    // step 3

    
}
