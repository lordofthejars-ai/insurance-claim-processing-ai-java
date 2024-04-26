package org.acme;

import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.infrastructure.Infrastructure;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class ClaimExtractorService {

    @Inject
    LlmService llmService;

    public Uni<ClaimParameters> extract(String text) {
        return Uni.createFrom()
                .item(() -> llmService.extract(text))
                .runSubscriptionOn(Infrastructure.getDefaultWorkerPool());
    }

}
