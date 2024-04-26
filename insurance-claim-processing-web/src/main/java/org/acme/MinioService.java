package org.acme;

import io.minio.*;
import io.minio.errors.*;
import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@ApplicationScoped
public class MinioService {

    @Inject
    MinioClient minio;

    @ConfigProperty(name = "minio.crash-images", defaultValue = "crash-images")
    String crashImagesBucketName;

    @ConfigProperty(name = "minio.processed-crash-images", defaultValue = "processed-crash-images")
    String processedCrashImagesBucketName;

    void onStart(@Observes StartupEvent ev) {
        this.deleteAndCreateBuckets();
    }

    public byte[] getProcessedCrashImage(String name) {
        return getCrashImage(name, processedCrashImagesBucketName);
    }

    byte[] getCrashImage(String name, String bucketName) {
        try (InputStream is = minio.getObject(
                GetObjectArgs.builder()
                        .bucket(bucketName)
                        .object(name)
                        .build());
        ) {
            return is.readAllBytes();
        } catch (MinioException | InvalidKeyException | IOException | NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }

    public void storeCrashImage(String id, byte[] image) {
        storeImage(id, image, crashImagesBucketName);
    }

    void storeImage(String id, byte[] image, String bucketName) {

        try {
            minio.putObject(
                    PutObjectArgs.builder().bucket(bucketName)
                            .object(id).stream(
                                    new ByteArrayInputStream(image), -1, 10485760)
                            .contentType("image/jpg")
                            .build());

        } catch (ErrorResponseException | InsufficientDataException | InternalException | InvalidResponseException |
                 IOException | NoSuchAlgorithmException | InvalidKeyException | ServerException | XmlParserException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public void deleteAndCreateBuckets() {
        deleteAndCreateBucket(crashImagesBucketName);
        deleteAndCreateBucket(processedCrashImagesBucketName);
    }

    private void deleteAndCreateBucket(String bucket) {
        try {
            boolean foundCrashImages =
                    minio.bucketExists(BucketExistsArgs.builder().bucket(bucket).build());

            if (foundCrashImages) {
                minio.removeBucket(RemoveBucketArgs.builder().bucket(bucket).build());
            }

            minio.makeBucket(
                    MakeBucketArgs.builder()
                            .bucket(bucket)
                            .build());

        } catch (ErrorResponseException | InsufficientDataException | InternalException | InvalidKeyException |
                 InvalidResponseException | IOException | NoSuchAlgorithmException | ServerException |
                 XmlParserException e) {
            throw new IllegalArgumentException(e);
        }
    }

}

