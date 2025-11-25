package deu.controller.business;

import deu.command.*;
import deu.model.dto.request.data.user.*;
import deu.model.dto.response.BasicResponse;
import deu.service.UserService;
import org.junit.jupiter.api.*;
import org.mockito.MockedStatic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@DisplayName("Command 패턴 통합 흐름 테스트 (Invoker -> Command -> UserService)")
public class UserManagementInvokerTest {

    private final UserManagementInvoker invoker = UserManagementInvoker.getInstance(); // Invoker
    
    private UserService mockUserService;
    private MockedStatic<UserService> mockedStatic;

    @BeforeEach
    void setUp() {
        // Receiver (UserService)의 Singleton 인스턴스를 Mocking 객체로 대체
        mockUserService = mock(UserService.class);
        mockedStatic = mockStatic(UserService.class);
        mockedStatic.when(UserService::getInstance).thenReturn(mockUserService);
        
        // Mock UserService의 예상 응답 설정
        when(mockUserService.update(any(UserDataModificationRequest.class))).thenReturn(new BasicResponse("200", "수정 성공"));
        when(mockUserService.delete(any(DeleteRequest.class))).thenReturn(new BasicResponse("200", "삭제 성공"));
        when(mockUserService.find(any(FindRequest.class))).thenReturn(new BasicResponse("200", "조회 성공"));
        when(mockUserService.findAll()).thenReturn(new BasicResponse("200", "전체 조회 성공"));
    }

    @AfterEach
    void tearDown() {
        // 정적 Mocking 해제
        mockedStatic.close();
    }

    // --- 테스트 메서드 ---

    @Test
    @DisplayName("UpdateUserCommand: Invoker 실행 시 UserService.update()가 호출되는지 검증")
    void testExecuteUpdateUserCommand() {
        // Given
        UserDataModificationRequest payload = new UserDataModificationRequest("S101", "pw", "name", "major");
        UpdateUserCommand command = new UpdateUserCommand(payload);

        // When
        BasicResponse response = invoker.executeCommand(command);

        // Then
        // Invoker의 실행 결과 검증
        assertEquals("200", response.code);
        
        // 핵심 검증: Command가 Receiver의 update 메서드를 호출했는지 확인
        verify(mockUserService, times(1)).update(payload);
    }

    @Test
    @DisplayName("DeleteUserCommand: Invoker 실행 시 UserService.delete()가 호출되는지 검증")
    void testExecuteDeleteUserCommand() {
        // Given
        DeleteRequest payload = new DeleteRequest("S102");
        DeleteUserCommand command = new DeleteUserCommand(payload);

        // When
        BasicResponse response = invoker.executeCommand(command);

        // Then
        // 핵심 검증: Command가 Receiver의 delete 메서드를 호출했는지 확인
        verify(mockUserService, times(1)).delete(payload);
    }
    
    @Test
    @DisplayName("FindUserCommand: Invoker 실행 시 UserService.find()가 호출되는지 검증")
    void testExecuteFindUserCommand() {
        // Given
        FindRequest payload = new FindRequest("S103");
        FindUserCommand command = new FindUserCommand(payload);

        // When
        BasicResponse response = invoker.executeCommand(command);

        // Then
        // 핵심 검증: Command가 Receiver의 find 메서드를 호출했는지 확인
        verify(mockUserService, times(1)).find(payload);
    }
    
    @Test
    @DisplayName("FindAllUsersCommand: Invoker 실행 시 UserService.findAll()이 호출되는지 검증")
    void testExecuteFindAllUsersCommand() {
        // Given
        FindAllUsersCommand command = new FindAllUsersCommand();

        // When
        BasicResponse response = invoker.executeCommand(command);

        // Then
        // 핵심 검증: Command가 Receiver의 findAll 메서드를 호출했는지 확인
        verify(mockUserService, times(1)).findAll();
    }
}