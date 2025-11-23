package deu.repository;

import deu.model.entity.RoomReservation;
import lombok.Getter;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.nodes.Tag;
import org.yaml.snakeyaml.representer.Representer;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class ReservationRepository extends AbstractYamlRepository<RoomReservation,ReservationRepository.RoomReservationWrapper> {

    private static final String FILE_PATH =
            System.getProperty("user.dir") + File.separator + "data" + File.separator + "reservations.yaml";

    @Getter
    private static final ReservationRepository instance = new ReservationRepository();

    private final List<RoomReservation> roomReservationList = new ArrayList<>();
    // private final Yaml yaml;

    // YAML Wrapper 클래스 (YAML 상단에 키 유지)
    public static class RoomReservationWrapper {
        public List<RoomReservation> reservations = new ArrayList<>();
    }

    private ReservationRepository() {
        super(FILE_PATH,RoomReservationWrapper.class);  // 추상클래스 생성자 호출
        loadFromFile();
    }

    // 예약 저장
    public void save(RoomReservation reservation) {
        roomReservationList.add(reservation);
        saveAllToFile();    // 템플릿 메서드 호출
    }

    // 예약 삭제 (객체 기준)
    public void delete(RoomReservation reservation) {
        roomReservationList.remove(reservation);
        saveAllToFile();
    }

    // 예약 ID로 삭제
    public boolean deleteById(String id) {
        boolean result = roomReservationList.removeIf(r -> r.getId().equals(id));
        if (result) saveAllToFile();
        return result;
    }

    // 예약 ID로 조회
    public RoomReservation findById(String id) {
        return roomReservationList.stream()
                .filter(r -> r.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    // 사용자 ID로 전체 예약 조회
    public List<RoomReservation> findByUser(String userId) {
        List<RoomReservation> results = new ArrayList<>();
        for (RoomReservation r : roomReservationList) {
            if (r.getNumber().equals(userId)) {
                results.add(r);
            }
        }
        return results;
    }

    // 모든 예약 반환
    public List<RoomReservation> findAll() {
        return new ArrayList<>(roomReservationList);
    }

    // 중복 예약 체크
    public boolean isDuplicate(String date, String startTime, String lectureRoom) {
        for (RoomReservation r : roomReservationList) {
            if (r.getDate().equals(date)
                    && r.getStartTime().equals(startTime)
                    && r.getLectureRoom().equals(lectureRoom)) {
                return true;
            }
        }
        return false;
    }

    // 테스트용: 전체 예약 삭제
    public void clear() {
        roomReservationList.clear();
        saveAllToFile();
    }
    
    public void saveToFile(){
        saveAllToFile();    // 템플릿 메서드 랩핑
    }
    
    
    
    // 추상클래스 구현 부분
    /** SnakeYAML에 등록해야 하는 클래스 태그 설정 */
    @Override
    protected void setupClassTags(Representer representer){
        representer.addClassTag(RoomReservationWrapper.class,Tag.MAP);
        representer.addClassTag(RoomReservation.class, Tag.MAP);
    }

    /** 파일에서 읽어온 wrapper를 내부 리스트에 반영 */
    @Override
    protected void applyLoadedWrapper(RoomReservationWrapper wrapper){
        if(wrapper != null && wrapper.reservations != null){
            roomReservationList.clear();
            roomReservationList.addAll(wrapper.reservations);
        }
    }

    /** 현재 내부 리스트를 기반으로 wrapper를 만들어 저장 */
    @Override
    protected RoomReservationWrapper createWrapperForSave(){
        RoomReservationWrapper wrapper = new RoomReservationWrapper();
        wrapper.reservations = roomReservationList;
        return wrapper;
    }

    /** 내부에서 관리하는 실제 엔티티 리스트 */
    @Override
    protected List<RoomReservation> getEntityList() {
        return roomReservationList;
    }
}