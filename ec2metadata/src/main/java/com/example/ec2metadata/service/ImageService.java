package com.example.ec2metadata.service;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ScanRequest;
import com.amazonaws.services.dynamodbv2.model.ScanResult;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;
import com.example.ec2metadata.model.ImageMetadata;
import com.example.ec2metadata.repository.ImageMetadataRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Random;

@Service
public class ImageService {

    private static final Logger log = LoggerFactory.getLogger(ImageService.class);
    @Autowired
    private AmazonS3 amazonS3;

    @Autowired
    private ImageMetadataRepository imageMetadataRepository;

    private final static String bucketName = "your-s3-bucket-name";

    public S3Object downloadImage(String name) {
        return amazonS3.getObject(new GetObjectRequest(bucketName, name));
    }

    public ImageMetadata getImageMetadata(String name) {
        log.info("getImageMetadata::{}", name);
        return imageMetadataRepository.findById(name).orElse(null);
    }

    public ImageMetadata getRandomImageMetadata() {
        List<ImageMetadata> allMetadata = imageMetadataRepository.findAll();
        if (allMetadata.isEmpty()) {
            return null;
        }
        Random random = new Random();
        return allMetadata.get(random.nextInt(allMetadata.size()));
    }

    public void uploadImage(MultipartFile file) throws IOException {
        String fileName = file.getOriginalFilename();
        long size = file.getSize();
        String fileExtension = fileName.substring(fileName.lastIndexOf(".") + 1);
        String lastUpdateDate = new Date().toString();

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(size);

        amazonS3.putObject(new PutObjectRequest(bucketName, fileName, file.getInputStream(), metadata));

        ImageMetadata imageMetadata = new ImageMetadata(fileName, lastUpdateDate, size, fileExtension);
        imageMetadataRepository.save(imageMetadata);
    }

    public void deleteImage(String name) {
        amazonS3.deleteObject(new DeleteObjectRequest(bucketName, name));
        imageMetadataRepository.deleteById(name);
    }
}
