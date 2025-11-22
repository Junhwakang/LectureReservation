package deu.model.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class RoomReservation implements Serializable {
    private String id = UUID.randomUUID().toString(); // 예약 생성 시 UUID 자동 할당
    private String buildingName;
    private String floor;
    private String lectureRoom;

    private String number;
    private String status = "대기";

    private String title; // 제목
    private String description; // 설명

    private String date; // 날짜
    private String dayOfTheWeek; // 요일

    private String startTime;
    private String endTime;
    
    // SFR-211 : 예약 목적, 인원 수
    private String purpose;// 보강, 세미나, 개인학습, 조별학습
    
    // 참석 인원 SFR-213,215
    private int participantCount; 
    
    // SFR-217 수용 인원 
    private int capacity; 
    
    // SFR-405, SFR-406: 취소 원인
    private String cancellationReason;
    
    // SFR-403, SFR-404: 거부 원인
    private String rejectionReason;
}