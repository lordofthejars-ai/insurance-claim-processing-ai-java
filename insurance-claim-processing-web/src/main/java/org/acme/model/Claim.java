package org.acme.model;

import io.quarkus.mongodb.panache.PanacheMongoEntity;
import io.quarkus.mongodb.panache.common.MongoEntity;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@MongoEntity(collection = "claim")
public class Claim extends PanacheMongoEntity {

    public String name;
    public String policyNumber;
    public List<String> involvedPlates = new ArrayList<>();
    public String fullText;
    public String summary;
    public String typeOfDamage;

    public LocalDate accidentDate;

}
