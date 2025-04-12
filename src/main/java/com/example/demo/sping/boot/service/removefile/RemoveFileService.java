package com.example.demo.sping.boot.service.removefile;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.stereotype.Service;

import com.example.demo.sping.boot.config.Config;



@Service
public class RemoveFileService {
    private final Config config;

    public RemoveFileService (Config config) {
        this.config = config;
    }

    public boolean clearAllFilesInUploadPath() {
        Path uploadPath = Paths.get(config.getNginxUploadPath());

        if (!Files.exists(uploadPath) || !Files.isDirectory(uploadPath)) {
            return false;
        }

        try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(uploadPath)) {
            for (Path filePath : directoryStream) {
                Files.deleteIfExists(filePath);
            }
            return true;
        } catch (IOException e) {
            return false;
        }
    }
}
