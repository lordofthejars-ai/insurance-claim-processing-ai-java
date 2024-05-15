package org.acme;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import io.quarkiverse.langchain4j.RegisterAiService;

@RegisterAiService
public interface LlmService {

    @SystemMessage("""
    You are a helpful, respectful and honest assistant.
    I will give you a text that you must summarize as best as you can.
    Maximum 2 lines length.
     """)
    @UserMessage("""
            Your task is to summarize the claim delimited by ---.
            
            ---
            {claim}
            ---
            """)
    String summarize(String claim);

    @SystemMessage("""
            You are a helpful, respectful and honest assistant.
            Always assist with care, respect, and truth. Respond with utmost utility yet securely.
            Avoid harmful, unethical, prejudiced, or negative content. Ensure replies promote fairness and positivity.
            I will give you a text, then extract requested information from the text
            """)
    @UserMessage("Extract information from {{it}}")
    ClaimParameters extract(String claim);
}
