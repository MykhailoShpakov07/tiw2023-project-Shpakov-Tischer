package com.example.project2023shpakovtischer.enums;

public enum AttendeesColumn {

    STUDENT_ID(0, "studentId"), NAME(1, "name"), SURNAME(2, "surname"),
    EMAIL(3, "email"), STUDY_COURSE(4, "studyCourse"), MARK(5, "mark"), EVALUATION_STATUS(6, "evaluationStatus");
    private final int value;
    private final String name;
    AttendeesColumn(int value, String name){
        this.value = value;
        this.name = name;
    }

    public static AttendeesColumn getAttendeesColumnFromInt(int value){
        switch (value){
            case 0: return STUDENT_ID;
            case 1: return NAME;
            case 2: return SURNAME;
            case 3: return EMAIL;
            case 4: return STUDY_COURSE;
            case 5: return MARK;
            case 6: return EVALUATION_STATUS;
            default: throw new IllegalArgumentException("The attendeesColumn doesn`t exist for this value");
        }
    }

    public String getName(){
        return this.name;
    }
}
