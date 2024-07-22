package com.root.mailbox.domain.utils;

import com.root.mailbox.domain.exceptions.attachment.AttachmentNotFoundException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Objects;

@Component
@AllArgsConstructor
@Slf4j
public class AwsAdapter {
    private final S3Client s3Client;

    public String insertFileOnBucket(String bucketName, MultipartFile file, String fileNameBucket) {
        log.info("Upload started on Amazon S3");

        try {
            PutObjectRequest request = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(fileNameBucket)
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
            log.error("Error while uploading on Amazon S3...");
            System.out.println(exception.getMessage());
        }

        return getFileUrl(bucketName, fileNameBucket);
    }

    public ResponseInputStream<GetObjectResponse> getFileOnBucket(String bucketName, String fileName) {
        log.info("Get file started on Amazon S3");

        ResponseInputStream<GetObjectResponse> fileContent = null;

        try {
            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(fileName)
                .build();

            ResponseInputStream<GetObjectResponse> responseInputStream = s3Client.getObject(getObjectRequest);

            if (Objects.isNull(responseInputStream)) {
                throw new AttachmentNotFoundException();
            }

            fileContent = responseInputStream;
        } catch (NoSuchKeyException | AttachmentNotFoundException ex) {
            log.info("Attachment not found in Amazon S3...");
            throw new AttachmentNotFoundException();
        } catch (Exception exception) {
            log.info("Error while getting file on Amazon S3...");
            System.out.println(exception.getMessage());
        }

        log.info("Get file finished on Amazon S3");

        return fileContent;
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
