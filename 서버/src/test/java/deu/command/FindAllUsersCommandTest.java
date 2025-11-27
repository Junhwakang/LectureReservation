package deu.command;

import deu.model.dto.response.BasicResponse;
import deu.service.UserService;
import java.lang.reflect.Field;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class FindAllUsersCommandTest {

    @Test
    public void testOrderUp() throws Exception {
        System.out.println("\n================================================");
        System.out.println("[Test Start] FindAllUsersCommandTest 시작");
        
        // 1. Arrange
        FindAllUsersCommand instance = new FindAllUsersCommand();
        
        UserService mockService = mock(UserService.class);
        BasicResponse expectedResponse = mock(BasicResponse.class);
        when(expectedResponse.toString()).thenReturn("BasicResponse(UserList)");
        when(mockService.findAll()).thenReturn(expectedResponse);
        
        Field receiverField = FindAllUsersCommand.class.getDeclaredField("receiver");
        receiverField.setAccessible(true);
        receiverField.set(instance, mockService);
        System.out.println("[Step 1] Mock 객체 생성 및 주입 완료");

        // 2. Act
        System.out.println("[Step 2] Command.orderUp() 실행 중...");
        BasicResponse result = instance.orderUp();

        // 3. Assert
        System.out.println("[Step 3] 결과 검증 중...");
        System.out.println("   -> 예상 결과: " + expectedResponse);
        System.out.println("   -> 실제 결과: " + result);

        assertEquals(expectedResponse, result);
        verify(mockService, times(1)).findAll();
        
        System.out.println(">>> [SUCCESS] FindAllUsersCommandTest 테스트 통과!");
        System.out.println("================================================\n");
    }
}