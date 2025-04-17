package gov.epa.ghg.invdb.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.extern.log4j.Log4j2;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Request;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.services.s3.paginators.ListObjectsV2Iterable;

@Service
@Log4j2
public class S3Service {
    @Autowired
    private AwsAuthService authService;

    public ResponseInputStream<GetObjectResponse> getS3Object(String bucket, String key)
            throws Exception {
        StaticCredentialsProvider credentialsProvider = this.authService.getCredentials();
        S3Client s3 = S3Client.builder()
                .region(Region.US_EAST_1)
                .credentialsProvider(credentialsProvider)
                .build();
        GetObjectRequest request = GetObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .build();
        return s3.getObject(request);
    }

    public byte[] downloadFile(String bucket, String key) throws Exception {
        byte[] bytes = null;
        StaticCredentialsProvider credentialsProvider = this.authService.getCredentials();
        try (S3Client s3 = S3Client.builder()
                .region(Region.US_EAST_1)
                .credentialsProvider(credentialsProvider)
                .build()) {
            GetObjectRequest request = GetObjectRequest.builder()
                    .bucket(bucket)
                    .key(key)
                    .build();
            ResponseInputStream<GetObjectResponse> response = s3.getObject(request);

            bytes = response.readAllBytes();
        } catch (S3Exception exception) {
            log.error("An error occured: ", exception);
        }
        return bytes;
    }

    public void uploadFile(byte[] fileContents, String bucket, String key) throws Exception {
        StaticCredentialsProvider credentialsProvider = this.authService.getCredentials();
        try (S3Client s3 = S3Client.builder()
                .region(Region.US_EAST_1)
                .credentialsProvider(credentialsProvider)
                .build()) {
            PutObjectRequest request = PutObjectRequest.builder()
                    .bucket(bucket)
                    .key(key)
                    .build();
            PutObjectResponse response = s3.putObject(request, RequestBody.fromBytes(fileContents));

        } catch (S3Exception exception) {
            log.error("An error occured: ", exception);
        }
    }

    public void uploadFile(File file, String bucket, String key) throws Exception {
        StaticCredentialsProvider credentialsProvider = this.authService.getCredentials();
        try (S3Client s3 = S3Client.builder()
                .region(Region.US_EAST_1)
                .credentialsProvider(credentialsProvider)
                .build();
                InputStream fileStream = new FileInputStream(file)) {
            PutObjectRequest request = PutObjectRequest.builder()
                    .bucket(bucket)
                    .key(key)
                    .build();

            // Get the file's length, which is needed for the InputStream supplier
            long contentLength = file.length();
            // Create a RequestBody using the InputStream and the content length
            RequestBody requestBody = RequestBody.fromInputStream(fileStream, contentLength);

            // Execute the "putObject" call to upload the file
            PutObjectResponse response = s3.putObject(request, requestBody);
        } catch (S3Exception exception) {
            log.error("An error occured: ", exception);
        }
    }

    public String[] listFilesInBucket(String bucket) throws Exception {
        StaticCredentialsProvider credentialsProvider = this.authService.getCredentials();
        String[] keys = new String[] {};
        try (S3Client s3 = S3Client.builder()
                .region(Region.US_EAST_1)
                .credentialsProvider(credentialsProvider)
                .build()) {
            try {
                ListObjectsV2Request listRequest = ListObjectsV2Request.builder()
                        .bucket(bucket)
                        .maxKeys(1)
                        .build();

                ListObjectsV2Iterable listResponse = s3.listObjectsV2Paginator(listRequest);
                keys = listResponse.stream()
                        .flatMap(r -> r.contents().stream())
                        .map(content -> content.key())
                        .toArray(String[]::new);

            } catch (S3Exception e) {
                System.err.println(e.awsErrorDetails().errorMessage());
                System.exit(1);
            }
        }
        return keys;
    }
}
