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
public class WeeklyLimitReservationValidationStrategy implements ReservationValidationStrategy {
    private static final int LIMIT_PER_7_DAYS = 5;

    @Override
    public void validate(RoomReservationRequest request,
                         ReservationRepository repo,
                         List<RoomReservation> userReservations)
            throws ReservationValidationException {

        LocalDate today = LocalDate.now();
        LocalDate maxDate = today.plusDays(6);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        long countWithin7Days = userReservations.stream()
                .filter(r -> {
                    try {
                        LocalDate date = LocalDate.parse(r.getDate(), formatter);
                        return !date.isBefore(today) && !date.isAfter(maxDate);
                    } catch (Exception e) {
                        return false;
                    }
                })
                .count();

        if (countWithin7Days >= LIMIT_PER_7_DAYS) {
            throw new ReservationValidationException(
                    "오늘부터 7일 간 최대 5개의 예약만 가능합니다."
            );
        }
    }
}
