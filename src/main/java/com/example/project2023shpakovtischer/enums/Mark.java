package com.example.project2023shpakovtischer.enums;

public enum Mark {

    NON_DEFINITO(0), ASSENTE(15), RIMANDATO(16), RIPROVATO(17),
    DICIOTTO(18), DICIANNOVE(19), VENTI(20), VENTUNO(21), VENTIDUE(22), VENTITRE(23), VENTIQUATTRO(24),
    VENTICINQUE(25), VENTISEI(26), VENTISIETTE(27), VENTOTTO(28), VENTINOVE(29), TRENTA(30), TRENTAELODE(31);
    private final int value;

    Mark(int value) {
        this.value = value;
    }

    public static Mark getUserRoleFromInt(int value){
        switch (value){
            case 0: return NON_DEFINITO;
            case 15: return ASSENTE;
            case 16: return RIMANDATO;
            case 17: return RIPROVATO;
            case 18: return DICIOTTO;
            case 19: return DICIANNOVE;
            case 20: return VENTI;
            case 21: return VENTUNO;
            case 22: return VENTIDUE;
            case 23: return VENTITRE;
            case 24: return VENTIQUATTRO;
            case 25: return VENTICINQUE;
            case 26: return VENTISEI;
            case 27: return VENTISIETTE;
            case 28: return VENTOTTO;
            case 29: return VENTINOVE;
            case 30: return TRENTA;
            case 31: return TRENTAELODE;
            default: throw new IllegalArgumentException("The mark doesn`t exist for this value");
        }
    }

}
