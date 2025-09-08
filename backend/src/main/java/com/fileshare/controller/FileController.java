package com.fileshare.controller;

import com.fileshare.service.FileStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/files")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class FileController {
    
    private final FileStorageService fileStorageService;
    
    @Autowired
    public FileController(FileStorageService fileStorageService) {
        this.fileStorageService = fileStorageService;
    }
    
    @PostMapping("/upload")
    public ResponseEntity<Map<String, Object>> uploadFile(@RequestParam("file") MultipartFile file) {
        Map<String, Object> response = new HashMap<>();
        
        if (file.isEmpty()) {
            response.put("success", false);
            response.put("message", "Please select a file to upload");
            return ResponseEntity.badRequest().body(response);
        }
        
        try {
            String fileName = fileStorageService.storeFile(file);
            response.put("success", true);
            response.put("message", "File uploaded successfully");
            response.put("fileName", fileName);
            response.put("originalName", file.getOriginalFilename());
            response.put("size", file.getSize());
            return ResponseEntity.ok(response);
        } catch (Exception ex) {
            response.put("success", false);
            response.put("message", "Failed to upload file: " + ex.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    @GetMapping("/download/{fileName:.+}")
    public ResponseEntity<Resource> downloadFile(@PathVariable String fileName) {
        try {
            Resource resource = fileStorageService.loadFileAsResource(fileName);
            
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .header(HttpHeaders.CONTENT_DISPOSITION, 
                            "attachment; filename=\"" + resource.getFilename() + "\"")
                    .body(resource);
        } catch (Exception ex) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping("/list")
    public ResponseEntity<Map<String, Object>> listFiles() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            List<String> files = fileStorageService.getAllFiles();
            response.put("success", true);
            response.put("files", files);
            return ResponseEntity.ok(response);
        } catch (Exception ex) {
            response.put("success", false);
            response.put("message", "Failed to list files: " + ex.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    @DeleteMapping("/delete/{fileName:.+}")
    public ResponseEntity<Map<String, Object>> deleteFile(@PathVariable String fileName) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            fileStorageService.deleteFile(fileName);
            response.put("success", true);
            response.put("message", "File deleted successfully");
            return ResponseEntity.ok(response);
        } catch (Exception ex) {
            response.put("success", false);
            response.put("message", "Failed to delete file: " + ex.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> healthCheck() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "UP");
        response.put("service", "File Sharing API");
        response.put("message", "Service is running");
        return ResponseEntity.ok(response);
    }
}