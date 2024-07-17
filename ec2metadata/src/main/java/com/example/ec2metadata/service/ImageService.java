package com.example.ec2metadata.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.model.MessageAttributeValue;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import com.example.ec2metadata.model.ImageMetadata;
import com.example.ec2metadata.repository.ImageMetadataRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ImageService {

    private static final Logger log = LoggerFactory.getLogger(ImageService.class);

    private final AmazonS3 amazonS3;
    private final AmazonSQS amazonSQS;
    private final ImageMetadataRepository imageMetadataRepository;

    @Value("${aws.s3.bucket.name}")
    private String bucketName;

    @Value("${aws.sqs.url}")
    private String sqsQueueUrl;

    public S3Object downloadImage(String name) {
        return amazonS3.getObject(new GetObjectRequest(bucketName, name));
    }

    public ImageMetadata getImageMetadata(String name) {
        log.info("getImageMetadata::{}", name);
        return imageMetadataRepository.findById(name).orElse(null);
    }

    public ImageMetadata getRandomImageMetadata() {
        var allMetadata = imageMetadataRepository.findAll();
        if (allMetadata.isEmpty()) {
            return null;
        }
        var random = new Random();
        return allMetadata.get(random.nextInt(allMetadata.size()));
    }

    public String uploadImage(MultipartFile file) throws IOException {
        var fileName = file.getOriginalFilename();
        var size = file.getSize();
        assert fileName != null;
        var fileExtension = fileName.substring(fileName.lastIndexOf(".") + 1);
        var lastUpdateDate = new Date().toString();

        var metadata = new ObjectMetadata();
        metadata.setContentLength(size);
        log.info("uploadImage:: name {}, size {}, bucketName {}", fileName, size, bucketName);

        amazonS3.putObject(new PutObjectRequest(bucketName, fileName, file.getInputStream(), metadata));

        var imageMetadata = new ImageMetadata(fileName, lastUpdateDate, size, fileExtension);
        imageMetadataRepository.save(imageMetadata);

        var downloadUrl = String.format("%s/%s", bucketName, fileName);
        // Publish message to SQS
        var messageAttributes = new HashMap<String, String>();
        messageAttributes.put("FileName", fileName);
        messageAttributes.put("FileSize", String.valueOf(file.getSize()));
        messageAttributes.put("FileExtension", getFileExtension(fileName));
        messageAttributes.put("DownloadUrl", downloadUrl);

        var sendMsgRequest = new SendMessageRequest()
                .withQueueUrl(sqsQueueUrl)
                .withMessageBody("Image uploaded: " + fileName)
                .withMessageAttributes(messageAttributes.entrySet().stream()
                        .collect(Collectors.toMap(Map.Entry::getKey, entry -> new MessageAttributeValue().withDataType("String").withStringValue(entry.getValue()))));
        amazonSQS.sendMessage(sendMsgRequest);

        return downloadUrl;
    }

    private String getFileExtension(String fileName) {
        return fileName.substring(fileName.lastIndexOf('.') + 1);
    }

    public void deleteImage(String name) {
        amazonS3.deleteObject(new DeleteObjectRequest(bucketName, name));
        imageMetadataRepository.deleteById(name);
    }
}
