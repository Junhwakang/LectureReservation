package deu.command;

import deu.model.dto.response.BasicResponse;
import java.io.Serializable;

/**
 * Command 인터페이스 (주문서)
 * - 모든 커맨드는 이 인터페이스를 구현해야 함
 * - orderUp() 메서드로 커맨드 실행
 * - Serializable: 네트워크를 통해 전송 가능
 */
public interface UserCommand extends Serializable {
    /**
     * 커맨드 실행 메서드 (주문 실행)
     * @return 실행 결과 (BasicResponse)
     */
    BasicResponse orderUp();
}