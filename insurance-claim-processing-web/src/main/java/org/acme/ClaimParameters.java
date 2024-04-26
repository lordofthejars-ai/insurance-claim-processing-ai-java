package org.acme;


import dev.langchain4j.model.output.structured.Description;

import java.time.LocalDate;
import java.util.List;

public record ClaimParameters (
        @Description("The full name of the person") String name,
        @Description("The policy number of the person") String policyNumber,
        @Description("All license plates involved in the incident") List<String> licensePlates,
        @Description("The date of the car accident") LocalDate accidentDate
        ) {}
