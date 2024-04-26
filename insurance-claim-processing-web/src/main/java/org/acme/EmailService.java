package org.acme;

import io.quarkus.logging.Log;
import io.quarkus.mailer.Mail;
import io.quarkus.mailer.Mailer;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class EmailService {

    @Inject
    Mailer mailer;

    public void sendAnEmail(String id, byte[] report) {
        Log.info("Sending an email: " + id);

        Mail mail = Mail
                .withText("sendMeALetter@quarkus.io", "Claim %s".formatted(id), "This is the report of case %s".formatted(id))
                .setFrom("origin@quarkus.io")
                .addAttachment("report.pdf", report, "application/pdf");

        mailer.send(mail);
    }

}