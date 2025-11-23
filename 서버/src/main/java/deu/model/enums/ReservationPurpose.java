/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package deu.model.enums;

/**
 *
 * @author User
 */


/*

// SFR-211,212 예약 목적 + 검증 216,218
*/
public enum ReservationPurpose {    
    SUPPLEMENT("보강"),
    SEMINAR("세미나"),
    PERSONAL_STUDY("개인학습"),
    GROUP_STUDY("조별학습");

    private final String label;

    ReservationPurpose(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public static ReservationPurpose fromLabel(String label) {
        for (ReservationPurpose p : values()) {
            if (p.label.equals(label)) {
                return p;
            }
        }
        throw new IllegalArgumentException("Unknown purpose: " + label);
    }
}
