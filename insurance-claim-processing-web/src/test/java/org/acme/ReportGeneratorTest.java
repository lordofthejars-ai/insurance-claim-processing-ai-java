package org.acme;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.acme.model.Claim;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.List;

@QuarkusTest
public class ReportGeneratorTest {

    @Inject
    ReportGenerator reportGenerator;

    @Test
    public void testReport() throws IOException {

        Claim claim = new Claim();
        claim.accidentDate = LocalDate.now();
        claim.typeOfDamage = "severe";
        claim.involvedPlates = List.of("DEF-456", "GHI-789");
        claim.summary = "Sarah Turner is filing a car insurance claim with Pacific Shield Insurance for a recent accident on January 2nd, 2024, providing details of the incident, damages to vehicles, and actions taken following the collision. She requests prompt attention and guidance for the claims process, attaching relevant documents for review and resolution.";
        claim.name = "Sarah Turner";
        claim.policyNumber = "AC-987654321";
        claim.fullText = """
                Dear Pacific Shield Insurance,
                                
                I hope this email finds you well. My name is Sarah Turner, and I am writing to file a claim for a recent car accident that occurred on January 2nd, 2024, at approximately 3:30 PM. My policy number is AC-987654321.
                                
                The accident took place at the intersection of Birch Street and Willow Avenue in the city of Evergreen. I was driving my vehicle, a black Toyota Camry with license plate number DEF-456, heading south on Birch Street. At the intersection, the traffic signal was green, and I proceeded through the intersection.
                                
                At the same time, another vehicle, a blue Chevrolet Traverse with license plate number GHI-789, was traveling west on Willow Avenue. Unfortunately, the driver failed to stop at the red traffic signal, resulting in a collision with the front passenger side of my vehicle.
                                
                The impact caused significant damage to both vehicles. The front bumper and right headlight of my Toyota Camry are extensively damaged, and there are also damages to the front driver's side of the Chevrolet Traverse. Fortunately, no injuries were sustained during the accident, and both drivers were able to move their vehicles to the side of the road.
                                
                I promptly exchanged information with the other driver, Mr. Daniel Reynolds, including our names, phone numbers, insurance details, and a brief description of the accident. Additionally, I took photos of the accident scene, including the damages to both vehicles and the position of the traffic signal.I have attached the necessary documents to this email, including the photos, a copy of the police report filed at the Evergreen Police Department, and the estimate for the repair costs from Evergreen Auto Repair, where I have taken my vehicle for assessment.
                                
                I kindly request your prompt attention to this matter and would appreciate any guidance on the next steps in the claims process. If you require any additional information or documentation, please do not hesitate to contact me at (555) 123-4567 or sarah.turner@email.com.
                                
                Thank you for your assistance, and I look forward to a swift resolution of this claim.
                                
                Sincerely,
                                
                Sarah Turner
                123 Oak Street
                Evergreen, CA 98765 (555) 123-4567
                sarah.turner@email.com"
                """.replaceAll("[\\n\\r]", "<br/>");

        byte[] image = Files.readAllBytes(Paths.get("src/test/resources/carImage3.jpg"));

        byte[] pdf = reportGenerator.report(claim, image);

        Files.write(Paths.get("target/report.pdf"), pdf);



    }

}
