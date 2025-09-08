package com.fileshare.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.stream.Stream;

@Service
public class FileStorageService {
    
    @Value("${file.upload-dir}")
    private String uploadDir;
    
    private Path uploadPath;
    
    @PostConstruct
    public void init() {
        this.uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize();
        try {
            Files.createDirectories(this.uploadPath);
        } catch (IOException ex) {
            throw new RuntimeException("Could not create upload directory!", ex);
        }
    }
    
    public String storeFile(MultipartFile file) {
        String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
        
        try {
            if (fileName.contains("..")) {
                throw new RuntimeException("Invalid filename: " + fileName);
            }
            
            Path targetLocation = uploadPath.resolve(fileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
            
            return fileName;
        } catch (IOException ex) {
            throw new RuntimeException("Could not store file " + fileName, ex);
        }
    }
    
    public Resource loadFileAsResource(String fileName) {
        try {
            Path filePath = uploadPath.resolve(fileName).normalize();
            Resource resource = new UrlResource(filePath.toUri());
            
            if (resource.exists()) {
                return resource;
            } else {
                throw new RuntimeException("File not found: " + fileName);
            }
        } catch (MalformedURLException ex) {
            throw new RuntimeException("File not found: " + fileName, ex);
        }
    }
    
    public List<String> getAllFiles() {
        try (Stream<Path> paths = Files.walk(uploadPath, 1)) {
            return paths
                    .filter(Files::isRegularFile)
                    .map(path -> path.getFileName().toString())
                    .toList();
        } catch (IOException ex) {
            throw new RuntimeException("Could not load files", ex);
        }
    }
    
    public void deleteFile(String fileName) {
        try {
            Path filePath = uploadPath.resolve(fileName).normalize();
            Files.deleteIfExists(filePath);
        } catch (IOException ex) {
            throw new RuntimeException("Could not delete file: " + fileName, ex);
        }
    }
}