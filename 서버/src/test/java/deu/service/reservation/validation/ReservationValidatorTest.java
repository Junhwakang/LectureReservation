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
class ReservationValidatorTest {

    private ReservationValidator validator;
    private ReservationRepository mockRepo;

    @BeforeEach
    void setUp() {
        mockRepo = mock(ReservationRepository.class);
        validator = new ReservationValidator()
                .addStrategy(new PurposeMaxValidationStrategy())
                .addStrategy(new WeeklyLimitReservationValidationStrategy());
    }

    @DisplayName("잘못된 요청이라도 예외 없이 처리되는지 확인")
    @Test
    @Order(1)
    void validate_badRequest_alsoDoesNotThrow() {
        RoomReservationRequest badRequest = new RoomReservationRequest();
        badRequest.setPurpose("보강");
        badRequest.setStartTime("10:00");
        badRequest.setEndTime("09:00");

        assertDoesNotThrow(()
                -> validator.validate(badRequest, mockRepo, List.of()));

        System.out.println("예외 처리되었습니다 - 시간 확인");
    }

    @DisplayName("정상 요청은 예외 없이 검증이 완료된다")
    @Test
    @Order(2)
    void validate_allStrategiesPass_passes() {
        RoomReservationRequest request = new RoomReservationRequest();
        request.setPurpose("보강");
        request.setStartTime("10:00");
        request.setEndTime("12:00");

        List<RoomReservation> userReservations = List.of();

        assertDoesNotThrow(()
                -> validator.validate(request, mockRepo, userReservations));

        System.out.println("정상적으로 검증되었음.");
    }

}
