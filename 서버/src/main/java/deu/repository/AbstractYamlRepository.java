/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package deu.repository;

import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.representer.Representer;

import java.io.*;
import java.util.List;

/**
 *
 * @author User
 */
public abstract class AbstractYamlRepository<T, W> {

    protected final String filePath;
    protected final Yaml yaml;
    private final Class<W> wrapperType;

    protected AbstractYamlRepository(String filePath, Class<W> wrapperType) {
        this.filePath = filePath;
        this.wrapperType = wrapperType;

        DumperOptions options = new DumperOptions();
        options.setPrettyFlow(true);
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);

        Representer representer = new Representer(options);
        representer.getPropertyUtils().setSkipMissingProperties(true);
        // 어떤 클래스 태그를 등록할지는 자식이 결정
        setupClassTags(representer);

        this.yaml = new Yaml(representer, options);

        createDataDirectoryIfNeeded();
        //loadFromFile();
    }

    // ----- 템플릿 공통 흐름 -----
    private void createDataDirectoryIfNeeded() {
        File file = new File(filePath);
        File parentDir = file.getParentFile();
        if (!parentDir.exists()) {
            parentDir.mkdirs();
        }
    }
    // 필요하면 자식이 override 해서 파일 없을 때 처리 (리소스에서 복사 등)
    protected boolean handleFileNotExists(File file) {
        return false;
    }

    protected final void loadFromFile() {   // 파일 불러오기
        File file = new File(filePath);
        if (!file.exists()) {
            // 파일이 없을 때 자식이 특별히 처리하고 싶으면 여기서 처리
            if (!handleFileNotExists(file)) {
                return; // 기본은 그냥 아무것도 안 함
            }
        }

        try (InputStream input = new FileInputStream(file)) {
            W wrapper = yaml.loadAs(input, wrapperType);
            applyLoadedWrapper(wrapper);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected final void saveAllToFile() {
        try (Writer writer = new FileWriter(filePath)) {
            W wrapper = createWrapperForSave();
            yaml.dump(wrapper, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // ----- 자식이 채우는 부분 (hook / abstract) -----
    /**
     * SnakeYAML에 등록해야 하는 클래스 태그 설정
     */
    protected abstract void setupClassTags(Representer representer);

    /**
     * 파일에서 읽어온 wrapper를 내부 리스트에 반영
     */
    protected abstract void applyLoadedWrapper(W wrapper);

    /**
     * 현재 내부 리스트를 기반으로 wrapper를 만들어 저장
     */
    protected abstract W createWrapperForSave();

    /**
     * 내부에서 관리하는 실제 엔티티 리스트
     */
    protected abstract List<T> getEntityList();
}
