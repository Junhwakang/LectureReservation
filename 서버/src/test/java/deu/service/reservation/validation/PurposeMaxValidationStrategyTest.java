/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package deu.service.reservation.validation;

/**
 *
 * @author User
 */
import deu.model.dto.request.data.reservation.RoomReservationRequest;
import deu.model.entity.RoomReservation;
import deu.repository.ReservationRepository;
import org.junit.jupiter.api.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class PurposeMaxValidationStrategyTest {

    private PurposeMaxValidationStrategy strategy;
    private ReservationRepository mockRepo;

    @BeforeEach
    void setUp() {
        strategy = new PurposeMaxValidationStrategy();
        mockRepo = mock(ReservationRepository.class);
    }

    private RoomReservationRequest createRequest(String purpose, String start, String end) {
        RoomReservationRequest req = new RoomReservationRequest();
        req.setPurpose(purpose);
        req.setStartTime(start);
        req.setEndTime(end);
        return req;
    }

    @DisplayName("보강은 최대 3시간까지만 허용")
    @Test
    @Order(1)
    void validate_supplementMaxThreeHours() {
        RoomReservationRequest valid = createRequest("보강", "09:00", "12:00");     // 3시간
        RoomReservationRequest invalid = createRequest("보강", "09:00", "13:00");   // 4시간

        assertDoesNotThrow(()
                -> strategy.validate(valid, mockRepo, List.of()));

        assertThrows(ReservationValidationException.class,
                () -> strategy.validate(invalid, mockRepo, List.of()));
    }

    @DisplayName("세미나는 최대 2시간까지만 허용")
    @Test
    @Order(2)
    void validate_seminarMaxTwoHours() {
        RoomReservationRequest valid = createRequest("세미나", "09:00", "11:00");   // 2시간 valid 허용
        RoomReservationRequest invalid = createRequest("세미나", "09:00", "12:00"); // 3시간 invalid 불가

        assertDoesNotThrow(()
                -> strategy.validate(valid, mockRepo, List.of()));

        assertThrows(ReservationValidationException.class,
                () -> strategy.validate(invalid, mockRepo, List.of()));
    }

    @DisplayName("개인 학습은 최대 4시간까지만 허용")
    @Test
    @Order(3)
    void validate_personalStudyMaxFourHours() {
        RoomReservationRequest valid = createRequest("개인 학습", "09:00", "13:00");
        RoomReservationRequest invalid = createRequest("개인 학습", "09:00", "14:00");

        assertDoesNotThrow(()
                -> strategy.validate(valid, mockRepo, List.of()));

        assertThrows(ReservationValidationException.class,
                () -> strategy.validate(invalid, mockRepo, List.of()));
    }

    @DisplayName("조별 학습은 최대 3시간까지만 허용")
    @Test
    @Order(4)
    void validate_groupStudyMaxThreeHours() {
        RoomReservationRequest valid = createRequest("조별 학습", "09:00", "12:00");
        RoomReservationRequest invalid = createRequest("조별 학습", "09:00", "13:00");

        assertDoesNotThrow(()
                -> strategy.validate(valid, mockRepo, List.of()));

        assertThrows(ReservationValidationException.class,
                () -> strategy.validate(invalid, mockRepo, List.of()));
    }

    @DisplayName("종료 시간이 시작 시간보다 빠르면 예외 발생")
    @Test
    @Order(5)
    void validate_endTimeBeforeStartTime_throwsException() {
        RoomReservationRequest invalid = createRequest("보강", "10:00", "09:00");

        assertThrows(ReservationValidationException.class,
                () -> strategy.validate(invalid, mockRepo, List.of()));
    }
}
