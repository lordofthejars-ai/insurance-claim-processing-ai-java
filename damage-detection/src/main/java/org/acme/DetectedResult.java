package org.acme;

public class DetectedResult {

    public String reportId;
    public String clazz;
    public String bucket;

    public DetectedResult() {
    }

    public DetectedResult(String reportId, String clazz, String bucket) {
        this.reportId = reportId;
        this.clazz = clazz;
        this.bucket = bucket;
    }
}
