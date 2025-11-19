/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package deu.service.reservation.validation;
import deu.model.dto.request.data.reservation.RoomReservationRequest;
import deu.model.entity.RoomReservation;
import deu.repository.ReservationRepository;

import java.util.ArrayList;
import java.util.List;
/**
 *
 * @author User
 */

//여러 전략을 묶기 SFR-211 도 addStrategy() 체이닝으로 추가
public class ReservationValidator {
    private final List<ReservationValidationStrategy> strategies = new ArrayList<>();
    
    public ReservationValidator addStrategy(ReservationValidationStrategy strategy){
        strategies.add(strategy);
        return this;
    }
    
    public void validate(RoomReservationRequest request,
                         ReservationRepository repo,
                         List<RoomReservation> userReservations)
            throws ReservationValidationException {

        for (ReservationValidationStrategy s : strategies) {
            s.validate(request, repo, userReservations);
        }
    }
}

