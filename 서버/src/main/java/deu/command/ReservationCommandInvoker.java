package deu.command;

import deu.model.dto.response.BasicResponse;
import lombok.Getter;

import java.util.Stack;

/**
 * 커맨드 실행 관리자 (Invoker)
 * 커맨드 실행, Undo/Redo 기능을 제공
 */
@Getter
public class ReservationCommandInvoker {
    private static final ReservationCommandInvoker instance = new ReservationCommandInvoker();

    public static ReservationCommandInvoker getInstance() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
    
    private final Stack<ReservationCommand> executedCommands = new Stack<>();
    private final Stack<ReservationCommand> undoneCommands = new Stack<>();
    
    private ReservationCommandInvoker() {}
    
    /**
     * 커맨드 실행
     */
    public BasicResponse executeCommand(ReservationCommand command) {
        BasicResponse response = command.execute();
        
        if ("200".equals(response.code)) {
            executedCommands.push(command);
            undoneCommands.clear(); // 새 커맨드 실행 시 Redo 스택 초기화
        }
        
        return response;
    }
    
    /**
     * 마지막 커맨드 실행 취소 (Undo)
     */
    public BasicResponse undo() {
        if (executedCommands.isEmpty()) {
            return new BasicResponse("400", "실행 취소할 작업이 없습니다.");
        }
        
        ReservationCommand command = executedCommands.pop();
        BasicResponse response = command.undo();
        
        if ("200".equals(response.code)) {
            undoneCommands.push(command);
        } else {
            // Undo 실패 시 다시 스택에 넣기
            executedCommands.push(command);
        }
        
        return response;
    }
    
    /**
     * 취소한 커맨드 재실행 (Redo)
     */
    public BasicResponse redo() {
        if (undoneCommands.isEmpty()) {
            return new BasicResponse("400", "재실행할 작업이 없습니다.");
        }
        
        ReservationCommand command = undoneCommands.pop();
        BasicResponse response = command.execute();
        
        if ("200".equals(response.code)) {
            executedCommands.push(command);
        } else {
            // Redo 실패 시 다시 스택에 넣기
            undoneCommands.push(command);
        }
        
        return response;
    }
    
    /**
     * 실행된 커맨드 히스토리 조회
     */
    public String getCommandHistory() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== 커맨드 히스토리 ===\n");
        
        for (int i = 0; i < executedCommands.size(); i++) {
            sb.append(i + 1).append(". ").append(executedCommands.get(i).getDescription()).append("\n");
        }
        
        return sb.toString();
    }
    
    /**
     * 히스토리 초기화
     */
    public void clearHistory() {
        executedCommands.clear();
        undoneCommands.clear();
    }
}
