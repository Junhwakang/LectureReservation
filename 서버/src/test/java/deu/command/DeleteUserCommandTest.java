package deu.command;

import deu.model.dto.request.data.user.DeleteRequest;
import deu.model.dto.response.BasicResponse;
import deu.service.UserService;
import java.lang.reflect.Field;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class DeleteUserCommandTest {
    
    @Test
    public void testOrderUp() throws Exception {
        System.out.println("\n================================================");
        System.out.println("[Test Start] DeleteUserCommandTest 시작");

        // 1. Arrange (준비)
        DeleteRequest payload = mock(DeleteRequest.class);
        DeleteUserCommand instance = new DeleteUserCommand(payload);
        
        UserService mockService = mock(UserService.class);
        BasicResponse expectedResponse = mock(BasicResponse.class);
        
        // Mock 동작 정의 (toString()을 정의하여 출력 시 보기 좋게 함)
        when(expectedResponse.toString()).thenReturn("BasicResponse(Success)");
        when(mockService.delete(payload)).thenReturn(expectedResponse);
        
        // Mock 주입 (Reflection)
        Field receiverField = DeleteUserCommand.class.getDeclaredField("receiver");
        receiverField.setAccessible(true);
        receiverField.set(instance, mockService);
        
        System.out.println("[Step 1] Mock 객체 생성 및 주입 완료");

        // 2. Act (실행)
        System.out.println("[Step 2] Command.orderUp() 실행 중...");
        BasicResponse result = instance.orderUp();

        // 3. Assert (검증)
        System.out.println("[Step 3] 결과 검증 중...");
        System.out.println("   -> 예상 결과: " + expectedResponse);
        System.out.println("   -> 실제 결과: " + result);
        
        assertEquals(expectedResponse, result);
        verify(mockService, times(1)).delete(payload);
        
        System.out.println(">>> [SUCCESS] DeleteUserCommandTest 테스트 통과!");
        System.out.println("================================================\n");
    }
}