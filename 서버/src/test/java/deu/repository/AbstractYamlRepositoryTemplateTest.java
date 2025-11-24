package deu.repository;

import org.junit.jupiter.api.*;
import org.yaml.snakeyaml.nodes.Tag;
import org.yaml.snakeyaml.representer.Representer;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class AbstractYamlRepositoryTemplateTest {

    // 테스트용 엔티티
    static class DummyEntity {
        public String id;
        public String name;

        public DummyEntity() {}
        public DummyEntity(String id, String name) {
            this.id = id;
            this.name = name;
        }
    }

    // 테스트용 Wrapper (YAML 최상단 루트)
    static class DummyWrapper {
        public List<DummyEntity> items = new ArrayList<>();
    }

    // AbstractYamlRepository 실제 구현체
    static class DummyRepository extends AbstractYamlRepository<DummyEntity, DummyWrapper> {

        private final List<DummyEntity> list = new ArrayList<>();

        protected DummyRepository(String path) {
            super(path, DummyWrapper.class);
        }

        @Override
        protected void setupClassTags(Representer representer) {
            // SnakeYAML 2.x: 클래스 태그를 MAP으로 등록해서 Global tag 방지
            representer.addClassTag(DummyWrapper.class, Tag.MAP);
            representer.addClassTag(DummyEntity.class, Tag.MAP);
        }

        @Override
        protected void applyLoadedWrapper(DummyWrapper wrapper) {
            if (wrapper != null && wrapper.items != null) {
                list.clear();
                list.addAll(wrapper.items);
            }
        }

        @Override
        protected DummyWrapper createWrapperForSave() {
            DummyWrapper w = new DummyWrapper();
            w.items = list;
            return w;
        }

        @Override
        protected List<DummyEntity> getEntityList() {
            return list;
        }
    }

    private DummyRepository repo;
    private File file;

    @BeforeEach
    void setup() {
        String path = System.getProperty("user.dir") + "/data/dummy.yaml";
        file = new File(path);
        if (file.exists()) {
            file.delete();
        }
        repo = new DummyRepository(path);
    }

    @AfterEach
    void cleanup() {
        if (file.exists()) {
            file.delete();
        }
    }

    @Test
    @DisplayName("Template Method: saveAllToFile -> YAML 파일 생성")
    void testTemplateSaveCreatesFile() {
        repo.getEntityList().add(new DummyEntity("D1", "템플릿테스트"));

        repo.saveAllToFile(); // 상위 템플릿 메서드 호출

        assertTrue(file.exists(), "템플릿 메서드를 통해 파일이 생성되어야 한다.");
    }

    @Test
    @DisplayName("Template Method: loadFromFile -> applyLoadedWrapper 호출 흐름 검증")
    void testTemplateLoadAppliesWrapper() {
        // 1) 먼저 한 번 저장
        repo.getEntityList().add(new DummyEntity("D1", "템플릿테스트"));
        repo.saveAllToFile();

        // 2) 새로운 인스턴스로 다시 로딩
        DummyRepository repo2 = new DummyRepository(file.getPath());
        repo2.loadFromFile(); // 상위 템플릿 메서드 호출

        assertEquals(1, repo2.getEntityList().size());
        assertEquals("D1", repo2.getEntityList().get(0).id);
    }
}
