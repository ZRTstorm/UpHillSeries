package climbing.climbBack.sensorData.service;

import climbing.climbBack.climbingData.service.ClimbingDataService;
import climbing.climbBack.sensorData.domain.SensorData;
import climbing.climbBack.sensorData.repository.SensorDataJpaRepository;
import climbing.climbBack.sensorData.repository.SensorDataMemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class SensorDataService {

    private final SensorDataJpaRepository jpaRepository;
    private final SensorDataMemRepository memRepository;
    private final ClimbingDataService climbingDataService;

    // 기록 시작
    public boolean setSensorList(SensorData sensorData) {
        Long routeId = sensorData.getRouteId();

        // 이미 Key 와 matching 되는 DataList 가 존재 하는 경우 -> 기록이 이미 시작된 경우
        // -> StartHold API 를 중복 호출한 경우
        if (memRepository.isDataList(routeId)) return false;

        memRepository.setDataList(sensorData);
        return true;
    }

    public void addSensorData(SensorData sensorData) {

        memRepository.addSensorData(sensorData);
    }

    @Transactional
    public boolean endSensorList(SensorData sensorData) {
        Long routeId = sensorData.getRouteId();

        // 이미 Key 와 matching 되는 DataList 를 없앤 경우 -> 기록을 완성한 경우
        // -> TopHold API 를 중복 호출한 경우
        if (!memRepository.isDataList(routeId)) return false;

        memRepository.addSensorData(sensorData);

        // memRepository 에서 해당 하는 route 의 dataList 를 삭제 + 가져 오기
        List<SensorData> sensorDataList = memRepository.removeDataList(routeId);

        // climbData 생성 -> SensorData 와 ClimbData 연관 -> 함께 영속
        Long climbDataId = climbingDataService.createClimbingData(sensorDataList, true);

        return true;
    }

    private Long timeSensing(List<SensorData> sensorDataList) {
        LocalDateTime firstTime = sensorDataList.get(0).getCreatedTime();
        LocalDateTime lastTime = sensorDataList.get(sensorDataList.size() - 1).getCreatedTime();

        Duration duration = Duration.between(firstTime, lastTime);
        return duration.getSeconds();
    }
}
