package deu.model.dto.request.data.reservation;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
public class RoomReservationRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    private String id;
    private String buildingName;
    private String floor;
    private String lectureRoom;
    private String title;
    private String description;
    private String date;
    private String dayOfTheWeek;
    private String startTime;
    private String endTime;
    private String number;
    private String status = "대기"; // 기본값 지정
    
    private String purpose;     // 보강, 세미나, 개인학습, 조별학습
    private int participantCount;   // 참석 인원 SFR-213,215
    private int capacity;
    
    public RoomReservationRequest(String buildingName, String floor, String lectureRoom,
            String title, String description, String date, String dayOfTheWeek,
            String startTime, String endTime, String number,String purpose, int participantCount) {
        this.buildingName = buildingName;
        this.floor = floor;
        this.lectureRoom = lectureRoom;
        this.title = title;
        this.description = description;
        this.date = date;
        this.dayOfTheWeek = dayOfTheWeek;
        this.startTime = startTime;
        this.endTime = endTime;
        this.number = number;
        this.status = "대기";
        this.purpose = purpose;
        this.participantCount = participantCount;
        this.capacity = capacity;
    }
    
    
}
