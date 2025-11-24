/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package deu.service.reservation.validation;

import deu.model.dto.request.data.reservation.RoomReservationRequest;
import deu.model.entity.RoomReservation;
import deu.repository.ReservationRepository;
import org.junit.jupiter.api.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

/**
 *
 * @author User
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ReservationValidatorTest {    // 모든 strategy 호출 통합 테스트

    private ReservationValidator validator;
    private ReservationRepository mockRepo;

    @BeforeEach
    void setUp() {
        mockRepo = mock(ReservationRepository.class);
        validator = new ReservationValidator()
                .addStrategy(new PurposeMaxValidationStrategy())
                .addStrategy(new WeeklyLimitReservationValidationStrategy());
    }

    @DisplayName("여러 전략 중 하나라도 실패하면 전체 검증이 실패한다")
    @Test
    @Order(1)
    void validate_anyStrategyFails_throwsException() {
        RoomReservationRequest badRequest = new RoomReservationRequest();
        badRequest.setPurpose("보강");
        badRequest.setStartTime("10:00");
        badRequest.setEndTime("09:00"); // 종료 시간이 더 빠름 → PurposeMax에서 예외

        assertThrows(ReservationValidationException.class,
                () -> validator.validate(badRequest, mockRepo, List.of()));
    }

    @DisplayName("등록된 모든 전략이 통과하면 예외 없이 검증이 완료된다")
    @Test
    @Order(2)
    void validate_allStrategiesPass_passes() {
        RoomReservationRequest request = new RoomReservationRequest();
        request.setPurpose("보강");
        request.setStartTime("10:00");
        request.setEndTime("12:00"); // 2시간 (보강 3시간 이내)

        List<RoomReservation> userReservations = List.of(); // 7일 이내 0건

        assertDoesNotThrow(()
                -> validator.validate(request, mockRepo, userReservations));
    }
}
