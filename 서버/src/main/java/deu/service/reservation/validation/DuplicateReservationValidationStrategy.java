/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package deu.service.reservation.validation;
import deu.model.dto.request.data.reservation.RoomReservationRequest;
import deu.model.entity.RoomReservation;
import deu.repository.ReservationRepository;
import java.util.*;
/**
 *
 * @author User
 */

/**
 * SFR-203
 * 서버는 예약 신청 시 중복 예약이 발생하지 않도록 검증할 수 있어야 한다.
 */
public class DuplicateReservationValidationStrategy implements ReservationValidationStrategy {
    @Override
    public void validate(RoomReservationRequest request,
                         ReservationRepository repo,
                         List<RoomReservation> userReservations)
            throws ReservationValidationException {

        // 1) 동일 사용자, 동일 날짜 & 시간대 중복
        for (RoomReservation r : userReservations) {
            if (r.getDate().equals(request.getDate()) &&
                r.getStartTime().equals(request.getStartTime())) {

                throw new ReservationValidationException(
                        "같은 시간대에 이미 예약이 존재합니다."
                );
            }
        }

        // 2) 같은 강의실, 같은 날짜 & 시간대 중복
        boolean isDup = repo.isDuplicate(
                request.getDate(),
                request.getStartTime(),
                request.getLectureRoom()
        );

        if (isDup) {
            throw new ReservationValidationException(
                    "해당 시간에 다른 예약이 존재합니다."
            );
        }
    }
}
