package org.acme;

import ai.djl.modality.cv.Image;
import ai.djl.modality.cv.ImageFactory;
import io.minio.*;
import io.minio.errors.*;
import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
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

    public Image getCrashImage(String name) {
        return getCrashImage(name, crashImagesBucketName);
    }

    Image getCrashImage(String name, String bucketName) {
        try (InputStream is = minio.getObject(
                GetObjectArgs.builder()
                        .bucket(bucketName)
                        .object(name)
                        .build());
        ) {
            return ImageFactory.getInstance().fromInputStream(is);
        } catch (MinioException | InvalidKeyException | IOException | NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }

    public void storeProcessedImage(String id, Image image) {
        storeImage(id, image, processedCrashImagesBucketName);
    }

    void storeImage(String id, Image image, String bucketName) {
        java.nio.file.Path output = null;

        try {

            output = Files.createTempFile("processed", id);
            try (OutputStream os = Files.newOutputStream(output)) {
                image.save(os, "png");
            }

            minio.putObject(
                    PutObjectArgs.builder().bucket(bucketName)
                            .object(id).stream(
                                    Files.newInputStream(output), -1, 10485760)
                            .contentType("image/png")
                            .build());

        } catch (ErrorResponseException | InsufficientDataException | InternalException | InvalidResponseException |
                 IOException | NoSuchAlgorithmException | InvalidKeyException | ServerException | XmlParserException e) {
            throw new IllegalArgumentException(e);
        } finally {
            if (output != null) {
                try {
                    Files.delete(output);
                } catch (IOException ignored) {
                }
            }
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
