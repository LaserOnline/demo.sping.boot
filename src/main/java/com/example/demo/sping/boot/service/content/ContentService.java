package com.example.demo.sping.boot.service.content;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.imageio.ImageIO;

import org.apache.tika.Tika;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.sping.boot.config.Config;
import com.example.demo.sping.boot.service.uuid.UuidService;
import com.example.demo.sping.boot.util.dto.UpdateContentDTO;
import com.example.demo.sping.boot.util.entity.ContentEntity;
import com.example.demo.sping.boot.util.entity.ContentImages;
import com.example.demo.sping.boot.util.entity.UsersEntity;
import com.example.demo.sping.boot.util.entity.UsersInfoEntity;
import com.example.demo.sping.boot.util.repository.ContentImagesRepository;
import com.example.demo.sping.boot.util.repository.ContentRepository;
import com.example.demo.sping.boot.util.repository.UserRepository;
import com.example.demo.sping.boot.util.repository.UsersInfoRepository;
import com.example.demo.sping.boot.util.response.ContentData;
import com.example.demo.sping.boot.util.response.CreateContentItem;

import net.coobird.thumbnailator.Thumbnails;

@Service
public class ContentService {
    private final Config config;
    private final Tika tika = new Tika();

    @Autowired
    private ContentRepository contentRepository;
    @Autowired
    private ContentImagesRepository contentImagesRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UsersInfoRepository usersInfoRepository;

    public ContentService(Config config) {
        this.config = config;
    }

    public ContentData getContentDataUuid(String contentUuid) {
        // ✅ Step 1: ดึง content จาก contentUuid
        ContentEntity content = contentRepository.findByContentUuid(contentUuid);
        if (content == null) return null;
    
        String userUuid = content.getUsersUuid();
    
        // ✅ Step 2: ตรวจสอบว่า userUuid มีอยู่จริง
        Optional<UsersEntity> userOpt = userRepository.findByUsersUuid(userUuid);
        Optional<UsersInfoEntity> infoOpt = usersInfoRepository.findByUsersUuid(userUuid);
    
        if (userOpt.isEmpty() || infoOpt.isEmpty()) {
            return null; // ❌ หากไม่พบ user หรือ info จบการทำงาน
        }
    
        // ✅ Step 3: ดึงข้อมูล username และ profile จาก user และ user_info
        String username = userOpt.get().getUsername();
        String profile = infoOpt.get().getProfile();
    
        // ✅ Step 4: ดึงรายชื่อไฟล์จาก contentUuid
        List<String> filenames = contentImagesRepository.findByContentUuid(contentUuid).stream()
            .flatMap(img -> img.getFile().stream())
            .collect(Collectors.toList());
    
        // ✅ Step 5: สร้างและคืนค่า ContentData
        return new ContentData(
            content.getContentUuid(),
            username,
            profile,
            content.getName(),
            content.getMessage(),
            filenames
        ); 
    }

    public List<ContentData> fetchAll() {
        List<ContentEntity> allContent = contentRepository.findAll();
        List<ContentData> result = new ArrayList<>();

    for (ContentEntity content : allContent) {
        String userUuid = content.getUsersUuid();

        // ตรวจสอบว่าผู้ใช้มีอยู่จริง
        Optional<UsersEntity> userOpt = userRepository.findByUsersUuid(userUuid);
        Optional<UsersInfoEntity> infoOpt = usersInfoRepository.findByUsersUuid(userUuid);

        if (userOpt.isEmpty() || infoOpt.isEmpty()) continue; // ข้ามถ้า user ไม่พบ

        String username = userOpt.get().getUsername();
        String profile = infoOpt.get().getProfile();

        List<String> filenames = contentImagesRepository.findByContentUuid(content.getContentUuid()).stream()
            .flatMap(img -> img.getFile().stream())
            .collect(Collectors.toList());

        result.add(new ContentData(
            content.getContentUuid(), 
            username,
            profile,
            content.getName(),
            content.getMessage(),
            filenames
        ));
    }
        return result; 
    }

    public boolean findContentUuid(String uuid) {
        return contentRepository.findByContentUuid(uuid) != null; 
    }

    public List<String> uploadFiles(List<String> base64List) {
        validateBase64Strings(base64List);                          // Step 1
        List<byte[]> decodedList = decodeAllBase64(base64List);     // Step 2
        validateAllMimeTypes(decodedList);                          // Step 3
        List<byte[]> jpegBytes = validateAndConvertToJpeg(decodedList); // Step 4
        List<BufferedImage> safeImages = parseBufferedImages(jpegBytes); // Step 5
        List<BufferedImage> resizedImages = resizeAllIfNeeded(safeImages); // Step 6
        List<String> filenames = saveAllImages(resizedImages);      // Step 7
        return filenames;                                           // Step 8
    }

    // step 1
    protected void validateBase64Strings(List<String> base64List) {
        for (int i = 0; i < base64List.size(); i++) {
            String base64 = base64List.get(i);
            if (base64 == null || base64.isBlank()) {
                throw new IllegalArgumentException("index image " + (i + 1) + ": null");
            }

            try {
                Base64.getDecoder().decode(base64);
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("index image" + (i + 1) + ": base64 incorrect", e);
            }
        }
    }

    // step 2
    protected List<byte[]> decodeAllBase64(List<String> base64List) {
        List<byte[]> result = new ArrayList<>();
        for (String base64 : base64List) {
            result.add(Base64.getDecoder().decode(base64));
        }
        return result;
    }

    // step 3
    protected void validateAllMimeTypes(List<byte[]> fileBytes) {
        for (int i = 0; i < fileBytes.size(); i++) {
            String mime = tika.detect(fileBytes.get(i));
            if (!mime.startsWith("image/")) {
                throw new IllegalArgumentException("index image " + (i + 1) + ": is not images");
            }
        }
    }

    // step 4
    protected List<byte[]> validateAndConvertToJpeg(List<byte[]> fileBytes) {
        List<byte[]> converted = new ArrayList<>();

        for (int i = 0; i < fileBytes.size(); i++) {
            byte[] bytes = fileBytes.get(i);
            String mime = tika.detect(bytes);

            if (mime.equals("image/jpeg")) {
                converted.add(bytes);
                continue;
            }

            try (ByteArrayInputStream bis = new ByteArrayInputStream(bytes)) {
                BufferedImage image = ImageIO.read(bis);
                if (image == null) {
                    throw new IllegalArgumentException("index image " + (i + 1) + ": Cannot read image for conversion");
                }

                File temp = File.createTempFile("img-convert-", ".jpg");
                Thumbnails.of(image)
                    .size(image.getWidth(), image.getHeight())
                    .outputFormat("jpg")
                    .toFile(temp);

                byte[] convertedBytes = java.nio.file.Files.readAllBytes(temp.toPath());
                converted.add(convertedBytes);

                temp.delete();

            } catch (IOException e) {
                throw new IllegalArgumentException("index image " + (i + 1) + ": Failed to convert to jpg", e);
            }
        }

        return converted;
    }

    // step 5
    protected List<BufferedImage> parseBufferedImages(List<byte[]> fileBytes) {
        List<BufferedImage> result = new ArrayList<>();
        for (int i = 0; i < fileBytes.size(); i++) {
            byte[] bytes = fileBytes.get(i);

            try (ByteArrayInputStream bisForImage = new ByteArrayInputStream(bytes)) {
                BufferedImage image = ImageIO.read(bisForImage);
                if (image == null) {
                    throw new IllegalArgumentException("index images " + (i + 1) + ": Unable to open as image");
                }
                result.add(image);
            } catch (IOException e) {
                throw new IllegalArgumentException("index images " + (i + 1) + ": Failed to read image", e);
            }
        }
        return result;
    }

    // step 5
    protected List<BufferedImage> resizeAllIfNeeded(List<BufferedImage> images) {
        List<BufferedImage> resized = new ArrayList<>();
        for (BufferedImage img : images) {
            if (img.getWidth() > 1920 || img.getHeight() > 1920) {
                resized.add(resizeImage(img));
            } else {
                resized.add(img);
            }
        }
        return resized;
    }

    // step 6
    protected BufferedImage resizeImage(BufferedImage image) {
        double scale = Math.min(1920.0 / image.getWidth(), 1920.0 / image.getHeight());
        int w = (int) (image.getWidth() * scale);
        int h = (int) (image.getHeight() * scale);

        try {
            return Thumbnails.of(image).size(w, h).asBufferedImage();
        } catch (IOException e) {
            throw new IllegalArgumentException("Resize fail", e);
        }
    }

    // step 7
    protected List<String> saveAllImages(List<BufferedImage> images) {
        List<String> filenames = new ArrayList<>();
        for (BufferedImage img : images) {
            String filename = generateNewFilename(); // ไม่มี .jpg
            String filenameWithExt = filename + ".jpg";
    
            saveImage(img, filenameWithExt);
            filenames.add(filename); 
        }
        return filenames;
    }

    // step 7.1
    protected String generateNewFilename() {
        return UuidService.generateUuidWithTimestamp();
    }

    // step 7.2
    protected void saveImage(BufferedImage image, String filename) {
        String savePath = Paths.get(config.getNginxUploadPath(), filename).toString();
        try {
            ImageIO.write(image, "jpg", new File(savePath));
        } catch (IOException e) {
            throw new IllegalArgumentException("Failed to save file: " + filename, e);
        }
    }


    // Create Content

    public void createContent (CreateContentItem create) {
        ContentEntity contentEntity = new ContentEntity();
        contentEntity.setContentUuid(create.getContentUuid());
        contentEntity.setUsersUuid(create.getUserUuid());
        contentEntity.setName(create.getName());
        contentEntity.setMessage(create.getMessage());
        contentRepository.save(contentEntity);

        // ถ้ามีไฟล์แนบให้สร้าง ContentImages
        if (create.getFiles() != null && !create.getFiles().isEmpty()) {
            ContentImages contentImages = new ContentImages();
            contentImages.setContentUuid(create.getContentUuid());
            contentImages.setFile(create.getFiles());
            contentImagesRepository.save(contentImages);
        }
    }


    public void updateContent(UpdateContentDTO dto, String userUuid) {
        // Step 1: ตรวจสอบว่า Content มีอยู่จริง
        ContentEntity content = contentRepository.findByContentUuid(dto.getUuid());
        if (content == null) {
            throw new IllegalArgumentException("Content not found");
        }
    
        // ✅ Step 1.1: ตรวจสอบว่า userUuid ตรงกับเจ้าของ Content
        if (!content.getUsersUuid().equals(userUuid)) {
            throw new IllegalArgumentException("Permission denied: not your content");
        }
    
        // Step 2: ตรวจสอบว่าไฟล์ใหม่ถูกอัปโหลดเข้ามา
        if (dto.getFiles() != null && !dto.getFiles().isEmpty()) {
            // ✅ ลบข้อมูล ContentImages เก่าใน MongoDB
            List<ContentImages> oldImages = contentImagesRepository.findByContentUuid(dto.getUuid());
            contentImagesRepository.deleteAll(oldImages);
        
            // ✅ ลบไฟล์ภาพเก่าออกจากระบบไฟล์
            for (ContentImages imageGroup : oldImages) {
                for (String filename : imageGroup.getFile()) {
                    String path = Paths.get(config.getNginxUploadPath(), filename + ".jpg").toString();
                    File file = new File(path);
                    if (file.exists()) {
                        file.delete(); // ลบไฟล์
                    }
                }
            }
        
            // ✅ อัปโหลดไฟล์ใหม่
            List<String> newFilenames = uploadFiles(dto.getFiles());
        
            // ✅ บันทึก ContentImages ใหม่
            ContentImages newImages = new ContentImages();
            newImages.setContentUuid(dto.getUuid());
            newImages.setFile(newFilenames);
            contentImagesRepository.save(newImages);
        }
    
        // Step 3: อัปเดต name และ message ถ้ามี
        boolean changed = false;
    
        if (dto.getName() != null && !dto.getName().isBlank()) {
            content.setName(dto.getName());
            changed = true;
        }
    
        if (dto.getMessage() != null && !dto.getMessage().isBlank()) {
            content.setMessage(dto.getMessage());
            changed = true;
        }
    
        if (changed) {
            contentRepository.save(content);
        }

    }

    public void deleteContent(String contentUuid, String userUuid) {
        // Step 1: ตรวจสอบว่า Content มีอยู่
        ContentEntity content = contentRepository.findByContentUuid(contentUuid);
        if (content == null) {
            throw new IllegalArgumentException("Content not found");
        }
    
        // Step 2: ตรวจสอบว่า userUuid ตรงกับเจ้าของ Content
        if (!content.getUsersUuid().equals(userUuid)) {
            throw new IllegalArgumentException("Permission denied: not your content");
        }
    
        // Step 3: ลบ ContentEntity
        contentRepository.delete(content);
    
        // Step 4: ลบ ContentImages + ลบไฟล์ภาพจริง
        List<ContentImages> imageGroups = contentImagesRepository.findByContentUuid(contentUuid);
        contentImagesRepository.deleteAll(imageGroups);
    
        for (ContentImages group : imageGroups) {
            for (String filename : group.getFile()) {
                String filePath = Paths.get(config.getNginxUploadPath(), filename + ".jpg").toString();
                File file = new File(filePath);
                if (file.exists()) {
                    file.delete(); // ลบไฟล์ .jpg
                }
            }
        }
    }
    
}
