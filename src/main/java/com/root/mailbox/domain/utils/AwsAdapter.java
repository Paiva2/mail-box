package com.root.mailbox.domain.utils;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Objects;
import java.util.UUID;

@Component
@AllArgsConstructor
@Slf4j
public class AwsAdapter {
    private final S3Client s3Client;

    public String insertFileOnBucket(String bucketName, MultipartFile file) {
        log.info("Upload started on Amazon S3");

        String fileName = "attachments_".concat(UUID.randomUUID().toString());

        try {
            PutObjectRequest request = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(fileName)
                .contentType(file.getContentType())
                .contentLength(file.getSize())
                .build();

            File convertedFile = convertMultiPartToFile(file);

            s3Client.putObject(request, Path.of(convertedFile.toURI()));

            if (convertedFile.exists()) {
                FileUtils.deleteQuietly(convertedFile);
            }

            log.info("Upload finished on Amazon S3");
        } catch (Exception exception) {
            log.info("Error while uploading on Amazon S3...");
            System.out.println(exception.getMessage());
        }

        return getFileUrl(bucketName, fileName);
    }

    public String getFileUrl(String bucket, String fileName) {
        StringBuilder builder = new StringBuilder();
        
        return builder.append("https://").append(bucket).append(".s3.amazonaws.com/").append(fileName).toString();
    }

    private File convertMultiPartToFile(MultipartFile multipartFile) throws IOException {
        if (Objects.isNull(multipartFile.getOriginalFilename())) throw new IOException();

        File file = new File(multipartFile.getOriginalFilename());

        FileOutputStream fos = new FileOutputStream(file);
        fos.write(multipartFile.getBytes());
        fos.close();

        return file;
    }
}
