/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Model;

/**
 *
 * @author jun
 */
public class Reservation {
    private String roomId;     // 강의실 번호
    private String date;       // 예약 날짜
    private String startTime;  // 예약 시작 시간
    private String endTime;    // 예약 종료 시간
    private String reservedBy; // 예약자 이름

    public Reservation(String roomId, String date, String startTime, String endTime, String reservedBy) {
        this.roomId = roomId;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.reservedBy = reservedBy;
    }

    public String getRoomId() {
        return roomId;
    }

    public String getDate() {
        return date;
    }

    public String getStartTime() {
        return startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public String getReservedBy() {
        return reservedBy;
    }

    @Override
    public String toString() {
        return String.format("%s | %s ~ %s | 예약자: %s", date, startTime, endTime, reservedBy);
    }
}