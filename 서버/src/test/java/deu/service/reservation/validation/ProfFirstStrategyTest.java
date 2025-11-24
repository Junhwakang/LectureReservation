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
class ProfFirstStrategyTest {

    private ProfFirstStrategy strategy;
    private ReservationRepository mockRepo;

    @BeforeEach
    void setUp() {
        strategy = new ProfFirstStrategy();
        mockRepo = mock(ReservationRepository.class);
    }

    private RoomReservationRequest createBaseRequest() {
        RoomReservationRequest req = new RoomReservationRequest();
        req.setDate("2025-05-30");
        req.setStartTime("10:00");
        req.setBuildingName("공대");
        req.setFloor("3");
        req.setLectureRoom("301");
        return req;
    }

    @DisplayName("예약자 번호가 교수이면 전략 검증을 수행하지 않는다")
    @Test
    @Order(1)
    void validate_requesterIsProfessor_skipsValidation() {
        RoomReservationRequest request = createBaseRequest();
        request.setNumber("p123"); // 교수

        assertDoesNotThrow(()
                -> strategy.validate(request, mockRepo, List.of()));
    }

    @DisplayName("교수 예약이 있는 시간+강의실에 학생이 예약하면 예외 발생")
    @Test
    @Order(2)
    void validate_professorReservationExistsForSameSlot_throwsException() {
        RoomReservationRequest request = createBaseRequest();
        request.setNumber("S123"); // 학생

        RoomReservation profRes = new RoomReservation();
        profRes.setNumber("P999");
        profRes.setStatus("승인");
        profRes.setDate("2025-05-30");
        profRes.setStartTime("10:00");
        profRes.setBuildingName("공대");
        profRes.setFloor("3");
        profRes.setLectureRoom("301");

        when(mockRepo.findAll()).thenReturn(List.of(profRes));

        assertThrows(ReservationValidationException.class,
                () -> strategy.validate(request, mockRepo, List.of()));
    }

    @DisplayName("취소된 교수 예약은 무시한다")
    @Test
    @Order(3)
    void validate_cancelledProfessorReservation_isIgnored() {
        RoomReservationRequest request = createBaseRequest();
        request.setNumber("S123");

        RoomReservation cancelledProfRes = new RoomReservation();
        cancelledProfRes.setNumber("P999");
        cancelledProfRes.setStatus("취소");
        cancelledProfRes.setDate("2025-05-30");
        cancelledProfRes.setStartTime("10:00");
        cancelledProfRes.setBuildingName("공대");
        cancelledProfRes.setFloor("3");
        cancelledProfRes.setLectureRoom("301");

        when(mockRepo.findAll()).thenReturn(List.of(cancelledProfRes));

        assertDoesNotThrow(()
                -> strategy.validate(request, mockRepo, List.of()));
    }

    @DisplayName("조건이 다른 교수 예약은 학생 예약을 막지 않는다")
    @Test
    @Order(4)
    void validate_differentSlotProfessorReservation_doesNotBlock() {
        RoomReservationRequest request = createBaseRequest();
        request.setNumber("S123");

        RoomReservation profRes = new RoomReservation();
        profRes.setNumber("P999");
        profRes.setStatus("승인");
        profRes.setDate("2025-05-30");
        profRes.setStartTime("11:00"); // 시간 다름
        profRes.setBuildingName("공대");
        profRes.setFloor("3");
        profRes.setLectureRoom("301");

        when(mockRepo.findAll()).thenReturn(List.of(profRes));

        assertDoesNotThrow(()
                -> strategy.validate(request, mockRepo, List.of()));
    }
}
