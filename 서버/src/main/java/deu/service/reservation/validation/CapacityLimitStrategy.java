/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package deu.service.reservation.validation;

import deu.model.dto.request.data.reservation.RoomReservationRequest;
import deu.model.entity.RoomReservation;
import deu.repository.ReservationRepository;

import java.util.List;

/**
 *
 * @author User
 */
public class CapacityLimitStrategy implements ReservationValidationStrategy {
    public void validate(RoomReservationRequest payload,
                         ReservationRepository repo,
                         List<RoomReservation> userReservations)
            throws ReservationValidationException {

        int capacity = payload.getCapacity();
        int count = payload.getParticipantCount();
        
        // 값이 제대로 안 들어왔으면 여기서는 그냥 패스 (다른 곳에서 검증해도 됨)
        if (capacity <= 0 || count <= 0) {
            capacity = 40;  // 임의로 지정. 강의실 대부분은 40인석으로 알고있기에
                            // 40이라는 정수로 지정함.
        }

        // SFR-217: 강의실 수용 인원의 50% 초과 불가
        // 예: 수용인원 40 → 최대 20명까지 허용
        if (count > capacity * 0.5) {
            throw new ReservationValidationException(
                    "예약 인원(" + count + "명)이 강의실 수용 인원(" + capacity + "명)의 50%를 초과할 수 없습니다");
        }
    }
}
