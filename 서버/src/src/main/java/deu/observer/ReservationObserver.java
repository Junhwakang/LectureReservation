package deu.observer;

import deu.model.entity.RoomReservation;

/**
 * 옵저버 패턴 - Observer 인터페이스
 * 예약 상태 변경 알림을 받는 인터페이스
 */
public interface ReservationObserver {
    /**
     * 예약 상태 변경 시 호출되는 메서드
     * @param reservation 변경된 예약 정보
     * @param message 알림 메시지
     */
    void update(RoomReservation reservation, String message);
}
