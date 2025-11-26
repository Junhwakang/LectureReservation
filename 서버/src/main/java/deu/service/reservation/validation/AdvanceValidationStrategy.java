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
 * SFR-214 서버는 개인/조별 학습 목적 예약이 최소 하루 전에 신청되었는지 검증해야 한다.
 */
public class AdvanceValidationStrategy implements ReservationValidationBehavior {

    private static final DateTimeFormatter FORMATTER
            = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Override
    public void validate(RoomReservationRequest payload,
            ReservationRepository repo,
            List<RoomReservation> userReservations)
            throws ReservationValidationException {

        String purpose = payload.getPurpose();
        if (purpose == null || purpose.isBlank()) {
            // 목적이 없으면 이 전략에서는 검사할 수 없음
            return;
        }

        // UI에서 들어오는 값까지 고려해서 비교
        // - "개인 학습" / "조별 학습" (공백 있음)
        // - "개인학습"   / "조별학습"   (공백 없음, 시간표에 이렇게 찍혀 있을 가능성 큼)
        boolean isLearningPurpose
                = "개인 학습".equals(purpose)
                || "조별 학습".equals(purpose)
                || "개인학습".equals(purpose)
                || "조별학습".equals(purpose);

        // 개인/조별 학습이 아니면 이 전략은 패스 (보강, 세미나 등)
        if (!isLearningPurpose) {
            return;
        }

        // 날짜 필수
        if (payload.getDate() == null || payload.getDate().isBlank()) {
            throw new ReservationValidationException("예약 날짜가 없습니다.");
        }

        try {
            LocalDate reservationDate = LocalDate.parse(payload.getDate(), FORMATTER);
            LocalDate today = LocalDate.now();

            // “최소 하루 전” = 예약 날짜 > 오늘
            //   - 예약일 == 오늘    → 실패
            //   - 예약일 <  오늘    → 실패
            //   - 예약일 >  오늘    → 통과
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
