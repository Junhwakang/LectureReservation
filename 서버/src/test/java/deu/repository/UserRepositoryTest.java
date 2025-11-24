package deu.repository;

import deu.model.dto.response.BasicResponse;
import deu.model.entity.User;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("UserRepository 단위 테스트")
class UserRepositoryTest {

    private UserRepository repo;

    @BeforeEach
    void setUp() {
        repo = UserRepository.getInstance();
    }

    @Test
    @DisplayName("회원가입 후 해당 학번으로 조회하면 사용자 정보가 반환된다")
    void testSaveAndFindByNumber() {
        // given
        String number = "T123456";
        String pw = "pw123";
        String name = "테스트유저";
        String major = "컴퓨터공학";

        // when
        BasicResponse saveResponse = repo.save(number, pw, name, major);
        BasicResponse findResponse = repo.findByNumber(number);

        // then
        assertEquals("200", saveResponse.code);
        assertEquals("200", findResponse.code);

        User user = (User) findResponse.data;
        assertEquals(number, user.number);
        assertEquals(name, user.name);
        assertEquals(major, user.major);

        // 정리
        repo.deleteByNumber(number);
    }

    @Test
    @DisplayName("같은 학번으로 두 번 회원가입하면 두 번째 요청은 실패해야 한다")
    void testDuplicateSave() {
        String number = "T999999";
        String pw = "pw";
        String name = "중복테스트";
        String major = "디자인패턴";

        BasicResponse first = repo.save(number, pw, name, major);
        BasicResponse second = repo.save(number, pw, name, major);

        assertEquals("200", first.code);
        assertEquals("400", second.code); // 이미 가입된 사용자 정보 입니다.

        // 정리
        repo.deleteByNumber(number);
    }

    @Test
    @DisplayName("로그인 성공/실패 케이스를 검증한다")
    void testValidate() {
        String number = "TLOGIN1";
        String pw = "pw123";
        repo.save(number, pw, "로그인유저", "소프트웨어");

        // 성공 케이스
        BasicResponse success = repo.validate(number, pw);
        assertEquals("200", success.code);

        // 비밀번호 오류
        BasicResponse wrongPw = repo.validate(number, "wrong");
        assertEquals("401", wrongPw.code);

        // 존재하지 않는 아이디
        BasicResponse notExist = repo.validate("NO_USER", "pw");
        assertEquals("400", notExist.code);

        // 정리
        repo.deleteByNumber(number);
    }

    @Test
    @DisplayName("사용자 삭제 후 존재 여부가 false가 된다")
    void testDeleteAndExists() {
        String number = "TDEL1";
        repo.save(number, "pw", "삭제유저", "전자공학");

        BasicResponse existsBefore = repo.existsByNumber(number);
        assertEquals("200", existsBefore.code);

        BasicResponse deleteResponse = repo.deleteByNumber(number);
        assertEquals("200", deleteResponse.code);

        BasicResponse existsAfter = repo.existsByNumber(number);
        assertEquals("404", existsAfter.code);
    }

    @Test
    @DisplayName("사용자 정보 수정이 정상적으로 반영된다")
    void testUpdate() {
        String number = "TUPDATE1";
        repo.save(number, "pw", "원래이름", "원래전공");

        BasicResponse updateResponse = repo.update(number, "newPw", "새이름", "새전공");
        assertEquals("200", updateResponse.code);

        BasicResponse findResponse = repo.findByNumber(number);
        User user = (User) findResponse.data;
        assertEquals("새이름", user.name);
        assertEquals("새전공", user.major);

        // 정리
        repo.deleteByNumber(number);
    }
}
