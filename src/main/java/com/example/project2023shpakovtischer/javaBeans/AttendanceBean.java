package com.example.project2023shpakovtischer.javaBeans;

import com.example.project2023shpakovtischer.enums.EvaluationStatus;
import com.example.project2023shpakovtischer.enums.Mark;

public class AttendanceBean {
    private int studentId;
    private int roundId;
    private String studentName;
    private String studentSurname;
    private String studentEmail;
    private String studentStudyCourse;
    private Mark mark;
    private EvaluationStatus evaluationStatus;

    public int getStudentId() {
        return studentId;
    }

    public void setStudentId(int studentId) {
        this.studentId = studentId;
    }

    public int getRoundId() {
        return roundId;
    }

    public void setRoundId(int roundId) {
        this.roundId = roundId;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getStudentSurname() {
        return studentSurname;
    }

    public void setStudentSurname(String studentSurname) {
        this.studentSurname = studentSurname;
    }

    public String getStudentEmail() {
        return studentEmail;
    }

    public void setStudentEmail(String studentEmail) {
        this.studentEmail = studentEmail;
    }

    public String getStudentStudyCourse() {
        return studentStudyCourse;
    }

    public void setStudentStudyCourse(String studentStudyCourse) {
        this.studentStudyCourse = studentStudyCourse;
    }

    public Mark getMark() {
        return mark;
    }

    public void setMark(Mark mark) {
        this.mark = mark;
    }

    public EvaluationStatus getEvaluationStatus() {
        return evaluationStatus;
    }

    public void setEvaluationStatus(EvaluationStatus evaluationStatus) {
        this.evaluationStatus = evaluationStatus;
    }


}
