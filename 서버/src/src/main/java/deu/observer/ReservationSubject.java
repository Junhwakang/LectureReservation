package deu.observer;

import deu.model.entity.RoomReservation;

import java.util.ArrayList;
import java.util.List;

/**
 * 옵저버 패턴 - Subject 클래스
 * 예약 상태 변경을 관찰하고 옵저버들에게 알림을 전달
 */
public class ReservationSubject {
    private final List<ReservationObserver> observers = new ArrayList<>();

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
     * 모든 옵저버에게 알림 전달
     */
    public void notifyObservers(RoomReservation reservation, String message) {
        for (ReservationObserver observer : observers) {
            observer.update(reservation, message);
        }
    }

    /**
     * 등록된 옵저버 수 반환
     */
    public int getObserverCount() {
        return observers.size();
    }

    /**
     * 모든 옵저버 제거
     */
    public void clearObservers() {
        observers.clear();
    }
}
