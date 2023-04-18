package com.example.project2023shpakovtischer.javaBeans;

import java.sql.Timestamp;

public class RoundBean {
    int roundId;
    Timestamp date;

    public int getRoundId() {
        return roundId;
    }

    public void setRoundId(int roundId) {
        this.roundId = roundId;
    }

    public Timestamp getDate() {
        return date;
    }

    public void setDate(Timestamp date) {
        this.date = date;
    }
}
