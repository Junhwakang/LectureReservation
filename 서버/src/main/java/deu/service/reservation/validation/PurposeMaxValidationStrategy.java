package deu.service.reservation.validation;

import deu.model.dto.request.data.reservation.RoomReservationRequest;
import deu.model.entity.RoomReservation;
import deu.repository.ReservationRepository;

import java.util.List;

/**
 * SFR-212 서버는 예약 목적에 따라 최대 예약 가능 시간을 제한해야 한다.
 *
 * - 한 번의 예약 길이 + 같은 날 같은 목적의 기존 예약 길이의 "총합"이 목적별 최대 시간을 넘으면 예외.
 */
public class PurposeMaxValidationStrategy implements ReservationValidationBehavior {

    @Override
    public void validate(RoomReservationRequest payload,
            ReservationRepository repo,
            List<RoomReservation> userReservations)
            throws ReservationValidationException {

        // 날짜/시간/목적이 없으면 여기서는 판단 불가 → 패스
        if (payload.getStartTime() == null
                || payload.getEndTime() == null
                || payload.getDate() == null
                || payload.getDate().isBlank()) {
            return;
        }

        String purpose = payload.getPurpose();
        if (purpose == null || purpose.isBlank()) {
            return; // 목적이 없으면 이 전략은 적용하지 않음
        }

        // 1) 이번 예약의 길이
        int newDuration = calcDuration(payload.getStartTime(), payload.getEndTime());
        if (newDuration <= 0) {
            throw new ReservationValidationException("종료 시간이 시작 시간보다 빠를 수 없습니다.");
        }

        int maxHours = getMaxHoursByPurpose(purpose);
        if (maxHours <= 0) {
            return; // 아직 제한이 없는 목적이면 패스
        }

        // 먼저 "한 번의 예약" 자체가 이미 초과인지 확인
        if (newDuration > maxHours) {
            throw new ReservationValidationException(
                    makeMessage(purpose, maxHours));
        }

        // 2) 같은 날 + 같은 목적의 기존 예약 시간 합산
        int totalHours = newDuration; // 이번 예약 포함

        if (userReservations != null) {
            for (RoomReservation r : userReservations) {
                if (r.getPurpose() == null) {
                    continue;
                }
                if (!purpose.equals(r.getPurpose())) {
                    continue;                // 목적 다르면 패스
                }
                if (r.getDate() == null) {
                    continue;
                }
                if (!payload.getDate().equals(r.getDate())) {
                    continue;        // 날짜 다르면 패스
                }
                if (r.getStartTime() == null || r.getEndTime() == null) {
                    continue;
                }

                int existingDuration = calcDuration(r.getStartTime(), r.getEndTime());
                if (existingDuration <= 0) {
                    continue;
                }

                totalHours += existingDuration;

                if (totalHours > maxHours) {
                    int before = totalHours - newDuration; // 기존 누적
                    throw new ReservationValidationException(
                            "해당 날짜에 " + purpose + " 예약은 최대 "
                            + maxHours + "시간까지 가능합니다. "
                            + "(기존 " + before + "시간 + 신규 " + newDuration + "시간)");
                }
            }
        }
    }

    // "HH:mm" 기준 단순 시(hour) 차이 계산 (UI가 1시간 단위라 이 정도면 충분)
    private int calcDuration(String start, String end) {
        try {
            int sh = Integer.parseInt(start.split(":")[0]);
            int eh = Integer.parseInt(end.split(":")[0]);
            return eh - sh;
        } catch (Exception e) {
            return 0;
        }
    }

    // 목적별 허용 최대 시간
    private int getMaxHoursByPurpose(String purpose) {
        return switch (purpose) {
            case "보강" ->
                3;
            case "세미나" ->
                2;
            case "개인 학습" ->
                4;
            case "조별 학습" ->
                3;
            default ->
                0; // 제한 없음
        };
    }

    private String makeMessage(String purpose, int max) {
        return switch (purpose) {
            case "보강" ->
                "보강은 최대 " + max + "시간까지 가능합니다.";
            case "세미나" ->
                "세미나는 최대 " + max + "시간까지 가능합니다.";
            case "개인 학습" ->
                "개인 학습은 최대 " + max + "시간까지 가능합니다.";
            case "조별 학습" ->
                "조별 학습은 최대 " + max + "시간까지 가능합니다.";
            default ->
                "해당 목적의 예약은 최대 " + max + "시간까지 가능합니다.";
        };
    }
}
