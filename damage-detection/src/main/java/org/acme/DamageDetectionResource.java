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
import java.nio.file.Paths;

@Path("/hello")
public class DamageDetectionResource {

    @Inject Predictor<Image, DetectedObjects> predictor;

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String hello() throws IOException, TranslateException {

        final java.nio.file.Path crashCar = Paths.get("src/test/resources/images/carImage3.jpg");
        final Image crashCarImg = ImageFactory.getInstance().fromFile(crashCar);

        DetectedObjects detectedObjects = predictor.predict(crashCarImg);

        System.out.println(detectedObjects.toString());
        return "Hello from Quarkus REST";
    }
}
