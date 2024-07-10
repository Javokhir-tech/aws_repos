package com.example.ec2metadata.api;

import com.example.ec2metadata.model.ImageMetadata;
import com.example.ec2metadata.service.ImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/image-metadata")
public class ImageMetadataApi {

    @Autowired
    private ImageService imageService;


    @GetMapping("/{name}")
    public ResponseEntity<ImageMetadata> getImageMetadata(@PathVariable String name) {
        ImageMetadata metadata = imageService.getImageMetadata(name);
        return ResponseEntity.ok(metadata);
    }

    @GetMapping("/random")
    public ResponseEntity<ImageMetadata> getRandomImageMetadata() {
        ImageMetadata metadata = imageService.getRandomImageMetadata();
        return ResponseEntity.ok(metadata);
    }

}
