package com.example.ec2metadata.api;

import com.amazonaws.services.s3.model.S3Object;
import com.example.ec2metadata.service.ImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

@RestController
@RequestMapping("/images")
public class ImageApi {

    @Autowired
    private ImageService imageService;

    @GetMapping("/{name}")
    public ResponseEntity<byte[]> downloadImage(@PathVariable String name) throws IOException {
        S3Object s3Object = imageService.downloadImage(name);
        InputStream inputStream = s3Object.getObjectContent();
        byte[] bytes = inputStream.readAllBytes();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + name + "\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(bytes);
    }

    @PostMapping("/upload")
    public ResponseEntity<String> uploadImage(@RequestParam("file") MultipartFile file) throws IOException {
        var downloadUrl = imageService.uploadImage(file);
        return ResponseEntity.ok("Image uploaded successfully " + downloadUrl);
    }

    @DeleteMapping("/{name}")
    public ResponseEntity<String> deleteImage(@PathVariable String name) {
        imageService.deleteImage(name);
        return ResponseEntity.ok("Image deleted successfully");
    }
}
