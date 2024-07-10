package com.example.ec2metadata.repository;

import com.example.ec2metadata.model.ImageMetadata;
import org.socialsignin.spring.data.dynamodb.repository.EnableScan;
import org.springframework.data.repository.CrudRepository;

@EnableScan
public interface ImageMetadataRepository extends CrudRepository<ImageMetadata, String> {
}

