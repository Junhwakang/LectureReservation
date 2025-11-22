package deu.observer;

import deu.model.entity.RoomReservation;

/**
 * 예약 이벤트 옵저버 인터페이스
 * 예약 상태 변경 시 알림을 받는 객체들이 구현
 */
public interface ReservationObserver {
    /**
     * 예약 승인 시 호출
     */
    void onReservationApproved(RoomReservation reservation);
    
    /**
     * 예약 거부 시 호출
     */
    void onReservationRejected(RoomReservation reservation, String reason);
    
    /**
     * 예약 취소 시 호출
     */
    void onReservationCancelled(RoomReservation reservation, String reason);
    
    /**
     * 예약 수정 시 호출
     */
    void onReservationModified(RoomReservation reservation);
    
    /**
     * 새 예약 신청 시 호출 (관리자 알림용)
     */
    void onReservationCreated(RoomReservation reservation);
}
