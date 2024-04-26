package org.acme;

import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import io.quarkus.qute.Template;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.acme.model.Claim;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;

@ApplicationScoped
public class ReportGenerator {

    @Inject
    Template claim;

    public byte[] report(Claim claimData, byte[] image) throws IOException {

        String htmlOutput =  this.claim
                .data("claim", claimData)
                .data("image", Base64.getEncoder().encodeToString(image))
                .render();

        try(ByteArrayOutputStream baos = new ByteArrayOutputStream()) {

            PdfRendererBuilder builder = new PdfRendererBuilder();
            builder.useFastMode();

            builder.withHtmlContent(htmlOutput, null);

            builder.toStream(baos);
            builder.run();

            return baos.toByteArray();
        }

    }

}
