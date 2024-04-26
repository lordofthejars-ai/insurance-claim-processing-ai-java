package org.acme;

import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.infrastructure.Infrastructure;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class SummarizeService {

    @Inject
    LlmService llmService;

    public Uni<String> summarize(String text) {
        return Uni.createFrom()
                .item(() -> llmService.summarize(text))
                .runSubscriptionOn(Infrastructure.getDefaultWorkerPool());
    }

}
