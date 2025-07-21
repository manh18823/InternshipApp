package com.example.internshipapp;

import java.util.Date;

public class Internship {
    private String title;
    private String recruiterId;
    private String company;
    private String location;
    private String duration;
    private String field;
    private String datePosted;
    private String description; // for detail screen

    public Internship() {} // Needed for Firestore

    public String getTitle() { return title; }
    public String getCompany() { return company; }
    public String getLocation() { return location; }
    public String getDuration() { return duration; }
    public String getField() { return field; }
    public String getDatePosted() { return datePosted; }
    public String getDescription() { return description; }

    public String getRecruiterId() {
        return recruiterId;
    }
}
