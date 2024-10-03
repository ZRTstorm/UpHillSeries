package climbing.climbBack.sensorData.repository;

import climbing.climbBack.sensorData.domain.SensorData;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class SensorDataMemRepository {

    // Key : RouteID , value : List<SensorData> -> 같은 루트에 설치된 센서 데이터
    private final ConcurrentHashMap<Long, List<SensorData>> hashMap;

    public SensorDataMemRepository() {
        this.hashMap = new ConcurrentHashMap<>();
    }

    // Key 에 해당 하는 value List 가 존재 하지 않는 경우 -> StartHold API 대응
    public void setDataList(SensorData sensorData) {
        List<SensorData> dataList = new ArrayList<>();

        Long routeId = sensorData.getRouteId();
        hashMap.put(routeId, dataList);

        // List 에 SensorData 추가
        dataList.add(sensorData);
    }

    // Key 에 해당 하는 value List 가 존재 하는지 확인
    // StartHold 중복 요청 케이스 검사용 매서드
    public boolean isDataList(Long routeId) {
        List<SensorData> dataList = hashMap.get(routeId);

        return dataList != null;
    }

    // Hold SensorData 저장
    public void addSensorData(SensorData sensorData) {
        Long routeId = sensorData.getRouteId();
        List<SensorData> dataList = hashMap.get(routeId);
        dataList.add(sensorData);
    }

    // Key 에 대응 하는 데이터 리스트 삭제 -> 데이터 리스트 반환
    public List<SensorData> removeDataList(Long routeId) {
        return hashMap.remove(routeId);
    }
}
