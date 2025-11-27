package deu.controller;

import static org.junit.jupiter.api.Assertions.*;

import deu.controller.business.UserController;
import deu.model.dto.request.data.user.LoginRequest;
import deu.model.dto.request.data.user.LogoutRequest;
import deu.model.dto.request.data.user.SignupRequest;
import deu.model.dto.request.command.UserCommandRequest;
import deu.model.dto.response.BasicResponse;
import deu.model.dto.response.CurrentResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import static org.mockito.Mockito.*;

public class SystemControllerTest {

    /* ================================
       기존 테스트(핵심 기능 호출 검증)
    =================================*/

    @Test
    @DisplayName("로그인 명령 처리: handleLogin 호출 확인")
    void handle_login_command_should_call_handleLogin() {

        System.out.println("\n[TEST] 로그인 명령 처리 테스트 시작");

        try (MockedStatic<UserController> mockedStatic = mockStatic(UserController.class)) {
            UserController mockUserController = mock(UserController.class);
            mockedStatic.when(UserController::getInstance).thenReturn(mockUserController);

            LoginRequest loginRequest = new LoginRequest("S2023001", "pw");
            BasicResponse expected = new BasicResponse("200", "로그인 성공");
            when(mockUserController.handleLogin(loginRequest)).thenReturn(expected);

            SystemController controller = new SystemController();
            Object response =
                    controller.handle(new UserCommandRequest("로그인", loginRequest));

            System.out.println("[RESULT] 반환값: " + response);

            assertTrue(response instanceof BasicResponse);
            assertEquals("200", ((BasicResponse) response).code);

            System.out.println("[TEST] 로그인 테스트 성공 ✔");
        }
    }

    @Test
    @DisplayName("회원가입 명령 처리: handleSignup 호출 확인")
    void handle_signup_command_should_call_handleSignup() {

        System.out.println("\n[TEST] 회원가입 명령 처리 테스트 시작");

        try (MockedStatic<UserController> mockedStatic = mockStatic(UserController.class)) {
            UserController mockUserController = mock(UserController.class);
            mockedStatic.when(UserController::getInstance).thenReturn(mockUserController);

            SignupRequest signupRequest =
                    new SignupRequest("S2023001", "pw", "홍길동", "컴공");
            BasicResponse expected = new BasicResponse("200", "회원가입 성공");
            when(mockUserController.handleSignup(signupRequest)).thenReturn(expected);

            SystemController controller = new SystemController();
            Object response =
                    controller.handle(new UserCommandRequest("회원가입", signupRequest));

            System.out.println("[RESULT] 반환값: " + response);

            assertTrue(response instanceof BasicResponse);
            assertEquals("200", ((BasicResponse) response).code);

            System.out.println("[TEST] 회원가입 테스트 성공 ✔");
        }
    }

    @Test
    @DisplayName("로그아웃 명령 처리: handleLogout 호출 확인")
    void handle_logout_command_should_call_handleLogout() {

        System.out.println("\n[TEST] 로그아웃 명령 처리 테스트 시작");

        try (MockedStatic<UserController> mockedStatic = mockStatic(UserController.class)) {
            UserController mockUserController = mock(UserController.class);
            mockedStatic.when(UserController::getInstance).thenReturn(mockUserController);

            LogoutRequest logoutRequest = new LogoutRequest("S2023001", "pw");
            BasicResponse expected = new BasicResponse("200", "로그아웃 성공");
            when(mockUserController.handleLogout(logoutRequest)).thenReturn(expected);

            SystemController controller = new SystemController();
            Object response =
                    controller.handle(new UserCommandRequest("로그아웃", logoutRequest));

            System.out.println("[RESULT] 반환값: " + response);

            assertTrue(response instanceof BasicResponse);
            assertEquals("200", ((BasicResponse) response).code);

            System.out.println("[TEST] 로그아웃 테스트 성공 ✔");
        }
    }

    @Test
    @DisplayName("동시접속자 수 요청 처리: handleCurrentUser 호출 확인")
    void handle_current_user_command_should_call_handleCurrentUser() {

        System.out.println("\n[TEST] 동시접속자 수 요청 테스트 시작");

        try (MockedStatic<UserController> mockedStatic = mockStatic(UserController.class)) {

            UserController mockUserController = mock(UserController.class);
            mockedStatic.when(UserController::getInstance).thenReturn(mockUserController);

            CurrentResponse expected = new CurrentResponse(3);
            when(mockUserController.handleCurrentUser()).thenReturn(expected);

            SystemController controller = new SystemController();
            Object response =
                    controller.handle(new UserCommandRequest("동시접속자", null));

            System.out.println("[RESULT] 반환값: " + response);

            assertTrue(response instanceof CurrentResponse);
            assertEquals(3, ((CurrentResponse) response).currentUserCount);

            System.out.println("[TEST] 동시접속자 테스트 성공 ✔");
        }
    }

    @Test
    @DisplayName("알 수 없는 명령 처리: 404 반환")
    void handle_unknown_command_should_return_404() {

        System.out.println("\n[TEST] 알 수 없는 명령 처리 테스트 시작");

        SystemController controller = new SystemController();
        Object response =
                controller.handle(new UserCommandRequest("삭제", null));

        System.out.println("[RESULT] 반환값: " + response);

        assertTrue(response instanceof BasicResponse);
        assertEquals("404", ((BasicResponse) response).code);

        System.out.println("[TEST] 알 수 없는 명령 처리 테스트 성공 ✔");
    }

    @Test
    @DisplayName("잘못된 타입 처리: 405 반환")
    void handle_invalid_type_should_return_405() {

        System.out.println("\n[TEST] 잘못된 타입 처리 테스트 시작");

        SystemController controller = new SystemController();
        Object response = controller.handle("이건 명령이 아님");

        System.out.println("[RESULT] 반환값: " + response);

        assertTrue(response instanceof BasicResponse);
        assertEquals("405", ((BasicResponse) response).code);

        System.out.println("[TEST] 잘못된 타입 처리 테스트 성공 ✔");
    }


    /* ================================
        퍼사드 패턴 정상동작 “추가 검증”
    =================================*/

    @Test
    @DisplayName("퍼사드 단일 진입점 테스트: 모든 기능은 handle() 하나로 처리된다")
    void facade_single_entry_point_test() {

        System.out.println("\n[FAÇADE TEST] 단일 인터페이스(handle) 테스트 시작");

        try (MockedStatic<UserController> mockedStatic = mockStatic(UserController.class)) {

            UserController mockUserController = mock(UserController.class);
            mockedStatic.when(UserController::getInstance).thenReturn(mockUserController);

            when(mockUserController.handleLogin(any()))
                    .thenReturn(new BasicResponse("200", "로그인 OK"));
            when(mockUserController.handleSignup(any()))
                    .thenReturn(new BasicResponse("200", "회원가입 OK"));
            when(mockUserController.handleLogout(any()))
                    .thenReturn(new BasicResponse("200", "로그아웃 OK"));

            SystemController facade = new SystemController();

            Object res1 = facade.handle(new UserCommandRequest("로그인",
                    new LoginRequest("A","B")));
            Object res2 = facade.handle(new UserCommandRequest("회원가입",
                    new SignupRequest("A","B","홍길동","컴공")));
            Object res3 = facade.handle(new UserCommandRequest("로그아웃",
                    new LogoutRequest("A","B")));

            System.out.println("[RESULT] 로그인 결과  : " + res1);
            System.out.println("[RESULT] 회원가입 결과: " + res2);
            System.out.println("[RESULT] 로그아웃 결과: " + res3);

            assertTrue(res1 instanceof BasicResponse);
            assertTrue(res2 instanceof BasicResponse);
            assertTrue(res3 instanceof BasicResponse);

            System.out.println("[FAÇADE TEST] 단일 진입점 테스트 성공 ✔");
        }
    }

    @Test
    @DisplayName("퍼사드는 내부 변경(UserController)과 무관하게 동일한 인터페이스로 동작한다")
    void facade_hides_internal_changes_test() {

        System.out.println("\n[FAÇADE TEST] 내부 구조 은닉 테스트 시작");

        try (MockedStatic<UserController> mockedStatic = mockStatic(UserController.class)) {

            UserController fake = mock(UserController.class);

            when(fake.handleLogin(any()))
                    .thenReturn(new BasicResponse("200", "내부로직 변경 OK"));

            mockedStatic.when(UserController::getInstance).thenReturn(fake);

            SystemController facade = new SystemController();

            Object res = facade.handle(new UserCommandRequest("로그인",
                    new LoginRequest("ID","PW")));

            System.out.println("[RESULT] 반환값: " + res);

            assertTrue(res instanceof BasicResponse);
            assertEquals("200", ((BasicResponse) res).code);
            System.out.println("[RESULT] 응답 내용: " + res.toString());


            System.out.println("[FAÇADE TEST] 내부 변경 은닉 테스트 성공 ✔");
        }
    }

    @Test
    @DisplayName("클라이언트는 퍼사드(SystemController)만으로 기능을 사용할 수 있다")
    void client_only_uses_facade_test() {

        System.out.println("\n[FAÇADE TEST] 클라이언트 의존성 감소 테스트 시작");

        try (MockedStatic<UserController> mockedStatic = mockStatic(UserController.class)) {

            UserController mockUserController = mock(UserController.class);
            mockedStatic.when(UserController::getInstance).thenReturn(mockUserController);

            when(mockUserController.handleSignup(any()))
                    .thenReturn(new BasicResponse("200", "OK"));

            SystemController facade = new SystemController();

            Object res = facade.handle(new UserCommandRequest("회원가입",
                    new SignupRequest("S01","pw","홍길동","컴공")));

            System.out.println("[RESULT] 반환값: " + res);

            assertTrue(res instanceof BasicResponse);

            System.out.println("[FAÇADE TEST] 클라이언트 의존성 감소 테스트 성공 ✔");
        }
    }
}
