package deu.command;

import deu.model.dto.response.BasicResponse;

/**
 * 예약 관리 커맨드 인터페이스
 * 모든 예약 관리 작업(수정, 삭제, 승인, 거부)을 캡슐화
 */
public interface ReservationCommand {
    /**
     * 커맨드 실행
     * @return BasicResponse 실행 결과
     */
    BasicResponse execute();
    
    /**
     * 커맨드 실행 취소 (Undo)
     * @return BasicResponse 실행 취소 결과
     */
    BasicResponse undo();
    
    /**
     * 커맨드 설명 반환
     * @return 커맨드 설명 문자열
     */
    String getDescription();
}
