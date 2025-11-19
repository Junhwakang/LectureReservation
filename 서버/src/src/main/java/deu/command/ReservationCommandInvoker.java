package deu.command;

import deu.model.dto.response.BasicResponse;

import java.util.ArrayList;
import java.util.List;

/**
 * 커맨드 패턴 - Invoker
 * 커맨드를 실행하고 히스토리를 관리하는 클래스
 */
public class ReservationCommandInvoker {
    private final List<ReservationCommand> commandHistory = new ArrayList<>();
    private int currentIndex = -1;

    /**
     * 커맨드 실행
     */
    public BasicResponse executeCommand(ReservationCommand command) {
        BasicResponse response = command.execute();
        
        // 성공 시에만 히스토리에 추가
        if ("200".equals(response.code)) {
            // 현재 인덱스 이후의 히스토리는 삭제 (새로운 커맨드 실행 시)
            if (currentIndex < commandHistory.size() - 1) {
                commandHistory.subList(currentIndex + 1, commandHistory.size()).clear();
            }
            
            commandHistory.add(command);
            currentIndex++;
        }
        
        return response;
    }

    /**
     * 마지막 커맨드 취소 (Undo)
     */
    public BasicResponse undo() {
        if (currentIndex < 0) {
            return new BasicResponse("400", "취소할 작업이 없습니다.");
        }

        ReservationCommand command = commandHistory.get(currentIndex);
        BasicResponse response = command.undo();
        
        if ("200".equals(response.code)) {
            currentIndex--;
        }
        
        return response;
    }

    /**
     * 취소한 커맨드 재실행 (Redo)
     */
    public BasicResponse redo() {
        if (currentIndex >= commandHistory.size() - 1) {
            return new BasicResponse("400", "재실행할 작업이 없습니다.");
        }

        currentIndex++;
        ReservationCommand command = commandHistory.get(currentIndex);
        BasicResponse response = command.execute();
        
        if (!"200".equals(response.code)) {
            currentIndex--;
        }
        
        return response;
    }

    /**
     * 히스토리 초기화
     */
    public void clearHistory() {
        commandHistory.clear();
        currentIndex = -1;
    }

    /**
     * 히스토리 크기 반환
     */
    public int getHistorySize() {
        return commandHistory.size();
    }
}
