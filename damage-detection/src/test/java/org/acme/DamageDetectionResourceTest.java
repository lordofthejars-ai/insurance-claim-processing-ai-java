package org.acme;

import ai.djl.modality.cv.Image;
import ai.djl.modality.cv.ImageFactory;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;

@QuarkusTest
class DamageDetectionResourceTest {

    @ConfigProperty(name = "minio.crash-images", defaultValue = "crash-images")
    String crashImagesBucketName;

    @ConfigProperty(name = "minio.processed-crash-images", defaultValue = "processed-crash-images")
    String processedCrashImagesBucketName;

    @Inject
    MinioService minioService;

    @Test
    void testDamageDetectionEndpoint() throws IOException {

        final Image image = ImageFactory.getInstance().fromFile(Paths.get("src/test/resources/images/carImage3.jpg"));
        minioService.storeImage("1234", image, crashImagesBucketName);

        given()
          .when().get("/damage/1234")
          .then()
             .statusCode(200)
                .body("clazz", equalTo("severe"));

        Image processed = minioService.getCrashImage("1234", processedCrashImagesBucketName);

        java.nio.file.Path outputPath = Paths.get("target/");
        Files.createDirectories(outputPath);
        java.nio.file.Path output = outputPath.resolve("crash_detected-test.png");
        try (OutputStream os = Files.newOutputStream(output)) {
            processed.save(os, "png");
        }
    }

}