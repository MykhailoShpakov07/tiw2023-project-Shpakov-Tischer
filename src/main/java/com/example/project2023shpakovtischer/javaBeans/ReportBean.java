package com.example.project2023shpakovtischer.javaBeans;

import java.sql.Date;
import java.sql.Timestamp;

public class ReportBean {
    private int reportCode;
    private Timestamp reportDateTime;
    private String courseName;
    private Date roundDate;

    public int getReportCode() {
        return reportCode;
    }

    public void setReportCode(int reportCode) {
        this.reportCode = reportCode;
    }

    public Timestamp getReportDateTime() {
        return reportDateTime;
    }

    public void setReportDateTime(Timestamp reportDateTime) {
        this.reportDateTime = reportDateTime;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public Date getRoundDate() {
        return roundDate;
    }

    public void setRoundDate(Date roundDate) {
        this.roundDate = roundDate;
    }
}
