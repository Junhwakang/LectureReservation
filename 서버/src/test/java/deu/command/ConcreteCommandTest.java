package deu.command;

import deu.model.dto.request.data.user.DeleteRequest;
import deu.model.dto.request.data.user.FindRequest;
import deu.model.dto.request.data.user.UserDataModificationRequest;
import deu.service.UserService;
import org.junit.jupiter.api.*;
import org.mockito.MockedStatic;

import static org.mockito.Mockito.*;

@DisplayName("Concrete Command 통합 단위 테스트 (Receiver 위임 검증)")
public class ConcreteCommandTest {

    // 테스트 전역에서 사용할 Mock 객체 및 정적 Mocking 환경
    private UserService mockUserService;
    private MockedStatic<UserService> mockedStatic;

    @BeforeEach
    void setUp() {
        // 모든 테스트에 앞서 UserService의 Singleton 인스턴스를 Mock 객체로 대체
        mockUserService = mock(UserService.class);
        mockedStatic = mockStatic(UserService.class);
        mockedStatic.when(UserService::getInstance).thenReturn(mockUserService);
    }

    @AfterEach
    void tearDown() {
        // 모든 테스트 후 정적 Mocking 환경 해제
        mockedStatic.close();
        // 각 테스트가 독립적으로 실행되도록 호출 기록 초기화 (Mockito가 자동으로 처리하지만 명시적으로)
        verifyNoMoreInteractions(mockUserService);
    }

    // --- 1. UpdateUserCommand 테스트 ---
    @Test
    @DisplayName("UpdateUserCommand: orderUp() 호출 시 UserService.update() 호출 검증")
    void testUpdateUserCommand() {
        // Given
        UserDataModificationRequest payload = new UserDataModificationRequest("S100", "pw", "Name", "Major");
        UpdateUserCommand command = new UpdateUserCommand(payload);

        // When
        command.orderUp();

        // Then
        // Receiver의 update 메서드가 정확히 한 번 호출되었는지 검증
        verify(mockUserService, times(1)).update(payload);
    }

    // --- 2. DeleteUserCommand 테스트 ---
    @Test
    @DisplayName("DeleteUserCommand: orderUp() 호출 시 UserService.delete() 호출 검증")
    void testDeleteUserCommand() {
        // Given
        DeleteRequest payload = new DeleteRequest("S101");
        DeleteUserCommand command = new DeleteUserCommand(payload);

        // When
        command.orderUp();

        // Then
        // Receiver의 delete 메서드가 정확히 한 번 호출되었는지 검증
        verify(mockUserService, times(1)).delete(payload);
    }

    // --- 3. FindUserCommand 테스트 ---
    @Test
    @DisplayName("FindUserCommand: orderUp() 호출 시 UserService.find() 호출 검증")
    void testFindUserCommand() {
        // Given
        FindRequest payload = new FindRequest("S102");
        FindUserCommand command = new FindUserCommand(payload);

        // When
        command.orderUp();

        // Then
        // Receiver의 find 메서드가 정확히 한 번 호출되었는지 검증
        verify(mockUserService, times(1)).find(payload);
    }

    // --- 4. FindAllUsersCommand 테스트 ---
    @Test
    @DisplayName("FindAllUsersCommand: orderUp() 호출 시 UserService.findAll() 호출 검증")
    void testFindAllUsersCommand() {
        // Given
        FindAllUsersCommand command = new FindAllUsersCommand();

        // When
        command.orderUp();

        // Then
        // Receiver의 findAll 메서드가 정확히 한 번 호출되었는지 검증
        verify(mockUserService, times(1)).findAll();
    }
}