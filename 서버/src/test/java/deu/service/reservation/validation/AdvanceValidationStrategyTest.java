/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package deu.service.reservation.validation;

import deu.model.dto.request.data.reservation.RoomReservationRequest;
import deu.repository.ReservationRepository;
import org.junit.jupiter.api.*;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

/**
 *
 * @author User
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class AdvanceValidationStrategyTest {

    private AdvanceValidationStrategy strategy;
    private ReservationRepository mockRepo;

    @BeforeEach
    void init() {
        strategy = new AdvanceValidationStrategy();
        mockRepo = mock(ReservationRepository.class);
    }

    private RoomReservationRequest createRequest(String purpose, String date) {
        RoomReservationRequest req = new RoomReservationRequest();
        req.setPurpose(purpose);
        req.setDate(date);
        return req;
    }

    @DisplayName("개인/조별 학습 예약은 당일이면 예외 발생")
    @Test
    @Order(1)
    void validate_learningPurposeToday_throwsException() {
        String today = LocalDate.now().toString();
        RoomReservationRequest req = createRequest("개인 학습", today);

        assertThrows(ReservationValidationException.class,
                () -> strategy.validate(req, mockRepo, List.of()));
    }

    @DisplayName("개인/조별 학습 예약은 과거 날짜면 예외 발생")
    @Test
    @Order(2)
    void validate_learningPurposePastDate_throwsException() {
        String yesterday = LocalDate.now().minusDays(1).toString();
        RoomReservationRequest req = createRequest("조별 학습", yesterday);

        assertThrows(ReservationValidationException.class,
                () -> strategy.validate(req, mockRepo, List.of()));
    }

    @DisplayName("개인/조별 학습 예약은 내일 이후 날짜라면 통과")
    @Test
    @Order(3)
    void validate_learningPurposeFutureDate_passes() {
        String tomorrow = LocalDate.now().plusDays(1).toString();
        RoomReservationRequest req = createRequest("개인 학습", tomorrow);

        assertDoesNotThrow(() ->
                strategy.validate(req, mockRepo, List.of()));
    }

    @DisplayName("목적이 학습 목적이 아니면 날짜 검증을 수행하지 않는다")
    @Test
    @Order(4)
    void validate_nonLearningPurpose_passes() {
        RoomReservationRequest req = createRequest("보강", LocalDate.now().toString());

        // 보강 목적일 경우 날짜 제한 없음 → 통과해야 정상
        assertDoesNotThrow(() ->
                strategy.validate(req, mockRepo, List.of()));
    }
}