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
import static org.mockito.Mockito.*;

/**
 *
 * @author User
 */

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class DuplicateReservationValidationStrategyTest {

    private DuplicateReservationValidationStrategy strategy;
    private ReservationRepository mockRepo;

    @BeforeEach
    void setUp() {
        strategy = new DuplicateReservationValidationStrategy();
        mockRepo = mock(ReservationRepository.class);
    }

    private RoomReservationRequest createBaseRequest() {
        RoomReservationRequest req = new RoomReservationRequest();
        req.setDate("2025-05-30");
        req.setStartTime("10:00");
        req.setLectureRoom("101");
        return req;
    }

    @DisplayName("같은 사용자가 같은 날짜+시간에 이미 예약한 경우 예외 발생")
    @Test
    @Order(1)
    void validate_userHasSameDateAndTime_throwsException() {
        RoomReservationRequest request = createBaseRequest();

        RoomReservation existing = new RoomReservation();
        existing.setDate("2025-05-30");
        existing.setStartTime("10:00");

        List<RoomReservation> userReservations = List.of(existing);

        assertThrows(ReservationValidationException.class,
                () -> strategy.validate(request, mockRepo, userReservations));
    }

    @DisplayName("같은 강의실 같은 날짜+시간에 다른 예약이 있으면 예외 발생")
    @Test
    @Order(2)
    void validate_lectureRoomHasDuplicate_throwsException() {
        RoomReservationRequest request = createBaseRequest();
        List<RoomReservation> userReservations = List.of(); // 사용자 본인 중복은 없음

        when(mockRepo.isDuplicate("2025-05-30", "10:00", "101"))
                .thenReturn(true);

        assertThrows(ReservationValidationException.class,
                () -> strategy.validate(request, mockRepo, userReservations));
    }

    @DisplayName("사용자 및 강의실 모두 중복이 없으면 통과")
    @Test
    @Order(3)
    void validate_noDuplicate_passes() {
        RoomReservationRequest request = createBaseRequest();
        List<RoomReservation> userReservations = List.of();

        when(mockRepo.isDuplicate(anyString(), anyString(), anyString()))
                .thenReturn(false);

        assertDoesNotThrow(()
                -> strategy.validate(request, mockRepo, userReservations));
    }
}
