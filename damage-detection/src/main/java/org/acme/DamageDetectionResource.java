package org.acme;

import ai.djl.inference.Predictor;
import ai.djl.modality.cv.Image;
import ai.djl.modality.cv.ImageFactory;
import ai.djl.modality.cv.output.DetectedObjects;
import ai.djl.translate.TranslateException;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

@Path("/hello")
public class DamageDetectionResource {

    @Inject Predictor<Image, DetectedObjects> predictor;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String detectCrash() throws IOException, TranslateException {

        final java.nio.file.Path crashCar = Paths.get("src/test/resources/images/carImage3.jpg");
        final Image crashCarImg = ImageFactory.getInstance().fromFile(crashCar);

        DetectedObjects detectedObjects = predictor.predict(crashCarImg);

        java.nio.file.Path outputPath = Paths.get("target/");
        Files.createDirectories(outputPath);

        if (detectedObjects.getNumberOfObjects() > 0) {
            crashCarImg.drawBoundingBoxes(detectedObjects);
            java.nio.file.Path output = outputPath.resolve("crash_detected.png");
            try (OutputStream os = Files.newOutputStream(output)) {
                crashCarImg.save(os, "png");
            }
        }

        return detectedObjects.toJson();
    }
}
