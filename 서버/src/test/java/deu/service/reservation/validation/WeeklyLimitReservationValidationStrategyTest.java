/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package deu.service.reservation.validation;

import deu.model.dto.request.data.reservation.RoomReservationRequest;
import deu.model.entity.RoomReservation;
import deu.repository.ReservationRepository;
import org.junit.jupiter.api.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

/**
 *
 * @author User
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class WeeklyLimitReservationValidationStrategyTest {

    private WeeklyLimitReservationValidationStrategy strategy;
    private ReservationRepository mockRepo;
    private final DateTimeFormatter formatter
            = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @BeforeEach
    void setUp() {
        strategy = new WeeklyLimitReservationValidationStrategy();
        mockRepo = mock(ReservationRepository.class);
    }

    @DisplayName("7일 이내 예약이 5개 미만이면 통과")
    @Test
    @Order(1)
    void validate_reservationsUnderWeeklyLimit_passes() {
        LocalDate today = LocalDate.now();
        List<RoomReservation> userReservations = new ArrayList<>();

        for (int i = 0; i < 4; i++) {
            RoomReservation r = new RoomReservation();
            r.setDate(today.plusDays(i).format(formatter));
            userReservations.add(r);
        }

        RoomReservationRequest request = new RoomReservationRequest();

        assertDoesNotThrow(()
                -> strategy.validate(request, mockRepo, userReservations));
    }

    @DisplayName("7일 이내 예약이 5개 이상이면 예외 발생")
    @Test
    @Order(2)
    void validate_reservationsReachWeeklyLimit_throwsException() {
        LocalDate today = LocalDate.now();
        List<RoomReservation> userReservations = new ArrayList<>();

        for (int i = 0; i < 5; i++) {
            RoomReservation r = new RoomReservation();
            r.setDate(today.plusDays(i).format(formatter));
            userReservations.add(r);
        }

        RoomReservationRequest request = new RoomReservationRequest();

        assertThrows(ReservationValidationException.class,
                () -> strategy.validate(request, mockRepo, userReservations));
    }

    @DisplayName("날짜 파싱이 실패하는 예약은 카운트에서 제외된다")
    @Test
    @Order(3)
    void validate_invalidDateIsIgnoredInCount() {
        LocalDate today = LocalDate.now();
        List<RoomReservation> userReservations = new ArrayList<>();

        for (int i = 0; i < 4; i++) {
            RoomReservation r = new RoomReservation();
            r.setDate(today.plusDays(i).format(formatter));
            userReservations.add(r);
        }

        RoomReservation invalidDateRes = new RoomReservation();
        invalidDateRes.setDate("invalid-date");
        userReservations.add(invalidDateRes);

        RoomReservationRequest request = new RoomReservationRequest();

        assertDoesNotThrow(()
                -> strategy.validate(request, mockRepo, userReservations));
    }
}
