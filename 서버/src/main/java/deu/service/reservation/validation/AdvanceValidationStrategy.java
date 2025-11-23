/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package deu.service.reservation.validation;

import deu.model.dto.request.data.reservation.RoomReservationRequest;
import deu.model.entity.RoomReservation;
import deu.repository.ReservationRepository;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 *
 * @author User
 */

/**
 * SFR-214
 * 서버는 개인/조별 학습 목적 예약이 최소 하루 전에 신청되었는지 검증해야 한다.
 */
public class AdvanceValidationStrategy implements ReservationValidationStrategy {
    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Override
    public void validate(RoomReservationRequest payload,
                         ReservationRepository repo,
                         List<RoomReservation> userReservations)
            throws ReservationValidationException {

        String purpose = payload.getPurpose();
        if (purpose == null) return;

        // 개인/조별 학습 목적일 때만 검증
        boolean isLearningPurpose =
                purpose.equals("개인 학습") || purpose.equals("조별 학습");

        if (!isLearningPurpose) return;

        if (payload.getDate() == null || payload.getDate().isBlank()) {
            throw new ReservationValidationException("예약 날짜가 없습니다.");
        }

        try {
            LocalDate reservationDate = LocalDate.parse(payload.getDate(), FORMATTER);
            LocalDate today = LocalDate.now();

            // “최소 하루 전” = 예약 날짜 > 오늘
            if (!reservationDate.isAfter(today)) {
                throw new ReservationValidationException(
                        "개인/조별 학습 예약은 최소 하루 전에 신청해야 합니다."
                );
            }

        } catch (Exception e) {
            throw new ReservationValidationException("예약 날짜 형식이 올바르지 않습니다.");
        }
    }
}
