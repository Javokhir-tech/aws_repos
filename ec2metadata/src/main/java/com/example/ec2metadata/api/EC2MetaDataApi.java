package com.example.ec2metadata.api;


import com.amazonaws.util.EC2MetadataUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class EC2MetaDataApi {

    @GetMapping("/metadata")
    public String getMetadata() {
        String availabilityZone = EC2MetadataUtils.getAvailabilityZone();
        String region = EC2MetadataUtils.getEC2InstanceRegion();
        return String.format("Region: %s, Availability Zone: %s", region, availabilityZone);
    }
}

