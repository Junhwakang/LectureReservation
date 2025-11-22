package deu.observer;

import deu.model.entity.RoomReservation;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

/**
 * 예약 Subject 클래스
 * 예약 상태 변경을 감지하고 옵저버들에게 알림
 */
@Getter
public class ReservationSubject {
    private static final ReservationSubject instance = new ReservationSubject();

    public static ReservationSubject getInstance() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
    
    private final List<ReservationObserver> observers = new ArrayList<>();
    
    private ReservationSubject() {}
    
    /**
     * 옵저버 등록
     */
    public void attach(ReservationObserver observer) {
        if (!observers.contains(observer)) {
            observers.add(observer);
        }
    }
    
    /**
     * 옵저버 제거
     */
    public void detach(ReservationObserver observer) {
        observers.remove(observer);
    }
    
    /**
     * 예약 승인 알림 (SFR-409)
     */
    public void notifyReservationApproved(RoomReservation reservation) {
        for (ReservationObserver observer : observers) {
            observer.onReservationApproved(reservation);
        }
    }
    
    /**
     * 예약 거부 알림 (SFR-409)
     */
    public void notifyReservationRejected(RoomReservation reservation, String reason) {
        for (ReservationObserver observer : observers) {
            observer.onReservationRejected(reservation, reason);
        }
    }
    
    /**
     * 예약 취소 알림 (SFR-410)
     */
    public void notifyReservationCancelled(RoomReservation reservation, String reason) {
        for (ReservationObserver observer : observers) {
            observer.onReservationCancelled(reservation, reason);
        }
    }
    
    /**
     * 예약 수정 알림 (SFR-412)
     */
    public void notifyReservationModified(RoomReservation reservation) {
        for (ReservationObserver observer : observers) {
            observer.onReservationModified(reservation);
        }
    }
    
    /**
     * 새 예약 신청 알림 (관리자에게 알림)
     */
    public void notifyReservationCreated(RoomReservation reservation) {
        for (ReservationObserver observer : observers) {
            observer.onReservationCreated(reservation);
        }
    }
}
