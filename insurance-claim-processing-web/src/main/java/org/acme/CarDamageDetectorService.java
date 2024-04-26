package org.acme;

import io.smallrye.mutiny.Uni;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@RegisterRestClient
@Path("/damage")
public interface CarDamageDetectorService {

    @GET
    @Path("/{reportId}")
    Uni<DetectedResult> detectCarDamage(@PathParam("reportId") String reportId);

}
