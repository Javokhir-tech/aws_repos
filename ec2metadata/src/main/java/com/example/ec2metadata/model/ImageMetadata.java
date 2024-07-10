package com.example.ec2metadata.model;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@DynamoDBTable(tableName = "ImageMetadata")
public class ImageMetadata {

    @DynamoDBHashKey
    private String name;
    private String lastUpdateDate;
    private long size;
    private String fileExtension;
}
