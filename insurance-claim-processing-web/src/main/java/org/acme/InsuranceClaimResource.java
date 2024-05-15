package org.acme;

import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.tuples.Tuple2;
import io.smallrye.mutiny.tuples.Tuple3;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.acme.model.Claim;
import org.bson.types.ObjectId;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.resteasy.reactive.PartType;
import org.jboss.resteasy.reactive.RestForm;
import org.jboss.resteasy.reactive.multipart.FileUpload;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.Files;
import java.util.Random;


@Path("/insurance")
public class InsuranceClaimResource {

    @Inject
    MinioService minioService;

    @RestClient
    CarDamageDetectorService carDamageDetectorService;

    @Inject
    ClaimExtractorService claimExtractorService;

    @Inject
    SummarizeService summarizeService;

    @Inject
    ReportGenerator reportGenerator;

    @Inject
    EmailService emailService;

    @GET
    @Path("/claim/{claimId}")
    public Claim findClainById(@PathParam("claimId") String claimId) {
        return Claim.findById(new ObjectId(claimId));
    }

    @GET
    @Path("/claim/image/{claimId}")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public byte[] getProcessedCrashImage(@PathParam("claimId") String claimId) {
        return getProcessedImage(claimId);
    }

    @POST
    @Path("/claim")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createClaim(@RestForm @PartType(MediaType.TEXT_PLAIN) String insuranceClaimReport,
                                @RestForm("picture") FileUpload picture) throws IOException {

        final String id = generateClaimId();
        minioService.storeCrashImage(id, getImage(picture));

        Tuple3<DetectedResult, String, ClaimParameters> tuple = useAI(insuranceClaimReport, id);

        DetectedResult dr = tuple.getItem1();
        String summary = tuple.getItem2();
        ClaimParameters claim = tuple.getItem3();

        Claim mongoClaim = createClaim(insuranceClaimReport, id, claim, dr, summary);
        mongoClaim.persist();

        byte[] pdfReport = reportGenerator.report(mongoClaim, getProcessedImage(id));
        emailService.sendAnEmail(id, pdfReport);

        printClaim(mongoClaim);

        return Response.ok().entity(id).build();
    }

    private Tuple3<DetectedResult, String, ClaimParameters> useAI(String insuranceClaimReport, String id) {
        Uni<DetectedResult> detectedResultUni = carDamageDetectorService.detectCarDamage(id);
        Uni<ClaimParameters> claimParamsUni = claimExtractorService.extract(insuranceClaimReport);

        Tuple2<DetectedResult, ClaimParameters> tuple = Uni.combine().all().unis(detectedResultUni, claimParamsUni)
                .asTuple()
                .await()
                .indefinitely();

        final DetectedResult detectedResult = tuple.getItem1();

        String summary = "Severe accident so no summary provided.";
        if (!"severe".equals(detectedResult.clazz())) {
            Uni<String> summarize = summarizeService.summarize(insuranceClaimReport);
            summary = summarize.await().indefinitely();
        }

        return Tuple3.of(detectedResult, summary, tuple.getItem2());

    }

    @NotNull
    private static Claim createClaim(String insuranceClaimReport, String id, ClaimParameters claim, DetectedResult dr, String summary) {
        Claim mongoClaim = new Claim();
        mongoClaim.id = new ObjectId(id);
        mongoClaim.fullText = insuranceClaimReport;

        mongoClaim.name = claim.name();
        mongoClaim.policyNumber = claim.policyNumber();
        mongoClaim.involvedPlates.addAll(claim.licensePlates());
        mongoClaim.typeOfDamage = dr.clazz();
        mongoClaim.summary = summary;
        mongoClaim.accidentDate = claim.accidentDate();
        return mongoClaim;
    }

    private static void printClaim(Claim mongoClaim) {
        System.out.println();
        System.out.println("***************");
        System.out.println("Name: " + mongoClaim.name);
        System.out.println("Policy Number: " + mongoClaim.policyNumber);
        System.out.println("Plates: " + mongoClaim.involvedPlates);
        System.out.println("Damage: " + mongoClaim.typeOfDamage);
        System.out.println("Accident Date: " + mongoClaim.accidentDate);
        System.out.println("Summary: " + mongoClaim.summary);
        System.out.println("***************");
        System.out.println();
    }


    private byte[] getProcessedImage(String id) {
        return this.minioService.getProcessedCrashImage(id);
    }


    private byte[] getImage(FileUpload picture) throws IOException {
        return Files.readAllBytes(picture.uploadedFile());
    }

    private String generateClaimId() {

        char[] charArr = new char[] {'0','1','2','3','4','5','6','7','8','9','a','b','c','d','e','f'};//hex digits array

        Random rand = new Random();
        StringBuilder result = new StringBuilder();
        for(int x = 0; x < 24; x++) {
            int resInt = rand.nextInt(charArr.length);
            result.append(charArr[resInt]);
        }
        return result.toString();
    }
}
