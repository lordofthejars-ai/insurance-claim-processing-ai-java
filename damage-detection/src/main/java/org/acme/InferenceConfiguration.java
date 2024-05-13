package org.acme;

import ai.djl.Application;
import ai.djl.MalformedModelException;
import ai.djl.inference.Predictor;
import ai.djl.modality.cv.Image;
import ai.djl.modality.cv.output.DetectedObjects;
import ai.djl.modality.cv.translator.YoloV8TranslatorFactory;
import ai.djl.repository.zoo.Criteria;
import ai.djl.repository.zoo.ModelNotFoundException;
import ai.djl.repository.zoo.ZooModel;
import ai.djl.training.util.ProgressBar;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Disposes;
import jakarta.enterprise.inject.Produces;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

@ApplicationScoped
public class InferenceConfiguration {

    @ConfigProperty(name = "fraud.model.path", defaultValue = "src/main/jib/model/accident-detect.onnx")
    private String modelPath;

    @Produces
    public Criteria<Image, DetectedObjects> criteria() {
        Path modelPath = Paths.get(this.modelPath);
        return
                Criteria.builder()
                        .optApplication(Application.CV.OBJECT_DETECTION)
                        .setTypes(Image.class, DetectedObjects.class)
                        .optModelPath(modelPath)
                        .optEngine("OnnxRuntime")
                        .optArgument("width", 640)
                        .optArgument("height", 640)
                        .optArgument("resize", true)
                        .optArgument("toTensor", true)
                        .optArgument("applyRatio", true)
                        // for performance optimization maxBox parameter can reduce number of
                        // considered boxes from 8400
                        .optArgument("maxBox", 1000)
                        .optTranslatorFactory(new YoloV8TranslatorFactory())
                        .optProgress(new ProgressBar())
                        .build();
    }

    @Produces
    public ZooModel<Image, DetectedObjects> zooModel(Criteria<Image, DetectedObjects>  criteria) throws ModelNotFoundException, MalformedModelException, IOException {
        return criteria.loadModel();
    }

    @Produces
    public Predictor<Image, DetectedObjects> predictor(ZooModel<Image, DetectedObjects>  zooModel) {
        return zooModel.newPredictor();
    }

    void close(@Disposes Predictor<Image, DetectedObjects>  predictor) {
        predictor.close();
    }

}
