/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package deu.service.reservation.validation;

import deu.model.dto.request.data.reservation.RoomReservationRequest;
import deu.model.entity.RoomReservation;
import deu.model.enums.ReservationPurpose;
import deu.repository.ReservationRepository;

import java.time.Duration;
import java.time.LocalTime;
import java.util.List;

/**
 *
 * @author User
 */

/**
 * SFR-212 서버는 예약 목적에 따라 최대 예약 가능 시간을 제한해야 한다.
 */
public class PurposeMaxValidationStrategy implements ReservationValidationStrategy {

    @Override
    public void validate(RoomReservationRequest payload,
                         ReservationRepository repo,
                         List<RoomReservation> userReservations)
            throws ReservationValidationException {

        if (payload.getStartTime() == null || payload.getEndTime() == null) {
            return; // 시간 정보 없으면 여기서는 패스
        }

        try {
            int startHour = Integer.parseInt(payload.getStartTime().split(":")[0]);
            int endHour = Integer.parseInt(payload.getEndTime().split(":")[0]);
            int duration = endHour - startHour;   // 단순 시간 차이

            if (duration <= 0) {
                throw new ReservationValidationException("종료 시간이 시작 시간보다 빠를 수 없습니다.");
            }

            String purpose = payload.getPurpose();
            if (purpose == null || purpose.isBlank()) {
                return; // 목적이 없으면 추가 제한 없음
            }

            switch (purpose) {
                case "보강" -> {
                    if (duration > 3) {
                        throw new ReservationValidationException("보강은 최대 3시간까지 가능합니다.");
                    }
                }
                case "세미나" -> {
                    if (duration > 2) {
                        throw new ReservationValidationException("세미나는 최대 2시간까지 가능합니다.");
                    }
                }
                case "개인 학습" -> {
                    if (duration > 4) {
                        throw new ReservationValidationException("개인 학습은 최대 4시간까지 가능합니다.");
                    }
                }
                case "조별 학습" -> {
                    if (duration > 3) {
                        throw new ReservationValidationException("조별 학습은 최대 3시간까지 가능합니다.");
                    }
                }
                default -> {
                    // 아직 기타적 제한 시간은 없지만 기본 2시간 정도로 생각중.
                }
            }
        } catch (NumberFormatException e) {
            // 시간 파싱 오류는 여기서 막지 않고 다른 곳에서 처리하도록 그냥 패스
        }
    }
}
