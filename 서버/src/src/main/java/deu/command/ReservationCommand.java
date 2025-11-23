package deu.command;

import deu.model.dto.response.BasicResponse;

/**
 * 커맨드 패턴 - Command 인터페이스
 * 예약 관리 작업을 캡슐화하는 인터페이스
 */
public interface ReservationCommand {
    /**
     * 커맨드 실행
     * @return 실행 결과
     */
    BasicResponse execute();
    
    /**
     * 커맨드 취소 (필요한 경우 구현)
     * @return 취소 결과
     */
    BasicResponse undo();
}
