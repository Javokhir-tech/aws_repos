package com.example.ec2metadata.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class ImageMetadata {

    @Id
    private String name;
    private String lastUpdateDate;
    private long size;
    private String fileExtension;
}
