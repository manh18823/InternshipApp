package com.example.internshipapp;

public class ApplicationItem {
    private String userId;
    private String internshipId;
    private String status;

    public ApplicationItem() {}  // Required for Firestore

    public ApplicationItem(String userId, String internshipId, String status) {
        this.userId = userId;
        this.internshipId = internshipId;
        this.status = status;
    }

    public String getUserId() { return userId; }
    public String getInternshipId() { return internshipId; }
    public String getStatus() { return status; }

    public void setUserId(String userId) { this.userId = userId; }
    public void setInternshipId(String internshipId) { this.internshipId = internshipId; }
    public void setStatus(String status) { this.status = status; }
}
