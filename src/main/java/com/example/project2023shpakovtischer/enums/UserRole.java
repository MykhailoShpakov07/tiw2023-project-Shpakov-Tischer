package com.example.project2023shpakovtischer.enums;

public enum UserRole {
    STUDENT(0), PROFESSOR(1);

    private final int value;

    UserRole(int value){
        this.value = value;
    }

    public static UserRole getUserRoleFromInt(int value){
        switch (value){
            case 0: return STUDENT;
            case 1: return PROFESSOR;
            default: throw new IllegalArgumentException("The userRole doesn`t exist for this value");
        }
    }

    public int getValue(){
        return this.value;
    }
}
