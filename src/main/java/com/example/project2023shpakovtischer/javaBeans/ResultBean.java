package com.example.project2023shpakovtischer.javaBeans;

import com.example.project2023shpakovtischer.enums.Mark;

public class ResultBean {
    private int studentId;
    private int roundId;
    private Mark mark;
    private EvaluationStatus status;

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

    public Mark getMark() {
        return mark;
    }

    public void setMark(Mark mark) {
        this.mark = mark;
    }

    public EvaluationStatus getStatus() {
        return status;
    }

    public void setStatus(EvaluationStatus status) {
        this.status = status;
    }
}
