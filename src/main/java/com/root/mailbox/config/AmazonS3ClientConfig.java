package com.root.mailbox.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

@Configuration
public class AmazonS3ClientConfig {
    @Value("${aws.region}")
    private String region;

    @Value("${aws.access.key}")
    private String accessKey;

    @Value("${aws.secret.key}")
    private String secretAccessKey;

    @Bean("s3Client")
    public S3Client s3Client() {
        return S3Client
            .builder()
            .credentialsProvider(buildCredentials())
            .region(Region.of(region))
            .build();
    }

    private StaticCredentialsProvider buildCredentials() {
        return StaticCredentialsProvider.create(AwsBasicCredentials.create(accessKey, secretAccessKey));
    }

}
