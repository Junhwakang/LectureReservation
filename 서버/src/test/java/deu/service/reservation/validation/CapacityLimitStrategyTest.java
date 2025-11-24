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
class CapacityLimitStrategyTest {

    private CapacityLimitStrategy strategy;
    private ReservationRepository mockRepo;

    @BeforeEach
    void setUp() {
        strategy = new CapacityLimitStrategy();
        mockRepo = mock(ReservationRepository.class);
    }

    private RoomReservationRequest createRequest(int capacity, int participantCount) {
        RoomReservationRequest req = new RoomReservationRequest();
        req.setCapacity(capacity);
        req.setParticipantCount(participantCount);
        return req;
    }

    @DisplayName("수용 인원의 50% 이하 예약 인원은 통과")
    @Test
    @Order(1)
    void validate_participantWithinHalfCapacity_passes() {
        RoomReservationRequest req = createRequest(40, 20); // 50%

        assertDoesNotThrow(()
                -> strategy.validate(req, mockRepo, List.of()));
    }

    @DisplayName("수용 인원의 50%를 초과하면 예외 발생")
    @Test
    @Order(2)
    void validate_participantExceedsHalfCapacity_throwsException() {
        RoomReservationRequest req = createRequest(40, 21); // 52.5%

        assertThrows(ReservationValidationException.class,
                () -> strategy.validate(req, mockRepo, List.of()));
    }

    @DisplayName("수용 인원 또는 인원이 0 이하일 경우 기본값 40 기준으로 검증")
    @Test
    @Order(3)
    void validate_invalidCapacityUsesDefault_throwsExceptionIfOverHalfOf40() {
        RoomReservationRequest req = createRequest(0, 30); // capacity <= 0 → 40으로 간주

        assertThrows(ReservationValidationException.class,
                () -> strategy.validate(req, mockRepo, List.of()));
    }

    @DisplayName("수용 인원 또는 인원이 0 이하이고 20명 이하면 통과")
    @Test
    @Order(4)
    void validate_invalidCapacityUsesDefault_passesIfUnderHalfOf40() {
        RoomReservationRequest req = createRequest(-1, 20); // 40의 50%

        assertDoesNotThrow(()
                -> strategy.validate(req, mockRepo, List.of()));
    }
}
