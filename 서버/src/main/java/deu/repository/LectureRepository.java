package deu.repository;

import deu.model.entity.Lecture;
import lombok.Getter;
import org.yaml.snakeyaml.nodes.Tag;
import org.yaml.snakeyaml.representer.Representer;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * 강의 정보를 YAML 파일로 관리하는 저장소 클래스
 * 싱글톤 패턴을 사용하며, 파일이 없을 경우 resources에서 복사하여 생성한다.
 * @author oixikite
 * @modifier oxxultus
 * @since 2025.05.16
 */
public class LectureRepository extends AbstractYamlRepository<Lecture, LectureRepository.LectureWrapper> {
    // 외부에서 접근하는 싱글톤 인스턴스

    // YAML 파일 경로 (JAR 또는 IDE 실행 경로 기준)
    private static final String FILE_PATH = System.getProperty("user.dir") + File.separator + "data" + File.separator + "lectures.yaml";
    // Singleton instance
    @Getter
    private static final LectureRepository instance = new LectureRepository();

    // 강의 리스트
    private final List<Lecture> lectureList = new ArrayList<>();

    // SnakeYAML 객체
    //private final Yaml yaml;

    // 강의 데이터를 감싸는 내부 클래스 - 아래 형식을 유지하기 위해 사용한다.
    public static class LectureWrapper {
        public List<Lecture> lectures;

        public LectureWrapper() {
            this.lectures = new ArrayList<>();
        }
    }

    // 생성자: YAML 설정 및 파일 로딩
    private LectureRepository() {
        super(FILE_PATH,LectureWrapper.class);
        loadFromFile();
    }

    // 강의 저장 (수정 포함)
    public String save(Lecture lecture) {
        if (lecture == null || lecture.getId() == null || lecture.getId().isBlank()) {
            return "400"; // 잘못된 요청
        }

        deleteById(lecture.getId());
        lectureList.add(lecture);
        saveAllToFile();
        return "200";
    }

    // 강의 삭제
    public String deleteById(String id) {
        boolean removed = lectureList.removeIf(l -> l.getId().equals(id));
        saveAllToFile();
        return removed ? "200" : "404";
    }

    // 강의 존재 여부
    public String existsById(String id) {
        return lectureList.stream().anyMatch(l -> l.getId().equals(id)) ? "200" : "404";
    }

    // 강의 ID로 조회
    public Optional<Lecture> findById(String id) {
        return lectureList.stream().filter(l -> l.getId().equals(id)).findFirst();
    }

    // 전체 강의 리스트 반환
    public List<Lecture> findAll() {
        return new ArrayList<>(lectureList);
    }

    // 강의명 + 교수명으로 ID 조회
    public Optional<String> findIdByLectureNameAndProfessor(String title, String professor) {
        return lectureList.stream()
                .filter(l -> l.getTitle().equals(title) && l.getProfessor().equals(professor))
                .map(Lecture::getId)
                .findFirst();
    }
    
    // ========= 템플릿 구현 부분
    @Override
    protected void setupClassTags(Representer representer) {
        representer.addClassTag(LectureWrapper.class, Tag.MAP);
        representer.addClassTag(Lecture.class, Tag.MAP);
    }

    @Override
    protected void applyLoadedWrapper(LectureWrapper wrapper) {
        if (wrapper != null && wrapper.lectures != null) {
            lectureList.clear();
            lectureList.addAll(wrapper.lectures);
        }
    }

    @Override
    protected LectureWrapper createWrapperForSave() {
        LectureWrapper wrapper = new LectureWrapper();
        wrapper.lectures = lectureList;
        return wrapper;
    }

    @Override
    protected List<Lecture> getEntityList() {
        return lectureList;
    }

    // 파일이 없으면 resources/data/lectures.yaml 을 복사
    @Override
    protected boolean handleFileNotExists(File file) {
        try {
            File parentDir = file.getParentFile();
            if (!parentDir.exists()) {
                parentDir.mkdirs();
            }

            try (InputStream resourceInput = getClass().getResourceAsStream("/data/lectures.yaml");
                 OutputStream output = new FileOutputStream(file)) {

                if (resourceInput == null) {
                    System.err.println("[LectureRepository] resources/data/lectures.yaml 리소스가 없습니다.");
                    return false;
                }

                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = resourceInput.read(buffer)) != -1) {
                    output.write(buffer, 0, bytesRead);
                }

                System.out.println("[LectureRepository] 리소스 파일 복사 완료");
                return true;
            }
        } catch (IOException e) {
            System.err.println("[LectureRepository] 리소스 파일 복사 중 오류: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}