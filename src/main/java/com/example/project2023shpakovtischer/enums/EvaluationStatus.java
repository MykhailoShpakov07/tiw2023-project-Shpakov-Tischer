package com.example.project2023shpakovtischer.enums;

public enum EvaluationStatus {
    NOT_INSERTED(0), INSERTED(1), PUBLISHED(2), REFUSED(3), REPORTED(4);

    private final int value;

    EvaluationStatus(int value){
        this.value = value;
    }

    public static EvaluationStatus getEvaluationStatusFromInt(int value){
        switch (value){
            case 0: return NOT_INSERTED;
            case 1: return INSERTED;
            case 2: return PUBLISHED;
            case 3: return REFUSED;
            case 4: return REPORTED;
            default: throw new IllegalArgumentException("The evaluationStatus doesn`t exist for this value");
        }
    }

    public int getValue(){
        return this.value;
    }

    public String toString(){
        switch (this.value){
            case 0: return "NON INSERITO";
            case 1: return "INSERITO";
            case 2: return "PUBBLICATO";
            case 3: return "RIFIUTATO";
            case 4: return "VERBALIZZATO";
            default: throw new IllegalArgumentException("The evaluationStatus doesn`t exist for this value");
        }
    }
}
