package com.example.internshipapp;

public class InterviewSlot {
    private String date;
    private String time;
    private boolean confirmed;

    public InterviewSlot() {}

    public String getDate() { return date; }
    public String getTime() { return time; }
    public boolean isConfirmed() { return confirmed; }

    public void setConfirmed(boolean confirmed) { this.confirmed = confirmed; }
}
