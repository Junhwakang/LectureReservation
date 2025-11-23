/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package deu.service.reservation.validation;

import deu.model.dto.request.data.reservation.RoomReservationRequest;
import deu.model.entity.RoomReservation;
import deu.repository.ReservationRepository;

import java.util.List;

/**
 *
 * @author User
 */

// 검증 전략 인터페이스
public interface ReservationValidationStrategy {

    void validate(RoomReservationRequest request,
            ReservationRepository repo,
            List<RoomReservation> userReservations) throws ReservationValidationException;
}
