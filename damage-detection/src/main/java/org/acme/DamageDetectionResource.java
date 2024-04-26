package org.acme;

import ai.djl.inference.Predictor;
import ai.djl.modality.cv.Image;
import ai.djl.modality.cv.ImageFactory;
import ai.djl.modality.cv.output.DetectedObjects;
import ai.djl.translate.TranslateException;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

@Path("/damage")
public class DamageDetectionResource {

    @Inject Predictor<Image, DetectedObjects> predictor;

    @Inject MinioService minioService;

    @GET
    @Path("/{reportId}")
    @Produces(MediaType.APPLICATION_JSON)
    public DetectedResult detectCrash(@PathParam("reportId") String reportId) throws TranslateException {

        //final java.nio.file.Path crashCar = Paths.get("src/test/resources/images/carImage3.jpg");
        //final Image crashCarImg = ImageFactory.getInstance().fromFile(crashCar);

        Image crashCarImg = minioService.getCrashImage(reportId);

        DetectedObjects detectedObjects = predictor.predict(crashCarImg);

        //java.nio.file.Path outputPath = Paths.get("target/");
        //Files.createDirectories(outputPath);

        if (detectedObjects.getNumberOfObjects() > 0) {

            crashCarImg.drawBoundingBoxes(detectedObjects);
            minioService.storeProcessedImage(reportId, crashCarImg);

            return new DetectedResult(reportId, detectedObjects.item(0).getClassName(), minioService.processedCrashImagesBucketName);

            /**java.nio.file.Path output = outputPath.resolve("crash_detected.png");
            try (OutputStream os = Files.newOutputStream(output)) {
                crashCarImg.save(os, "png");
            }**/
        }

        return new DetectedResult();

    }
}
