package climbing.climbBack.climbData.service;

import climbing.climbBack.climbData.domain.ClimbData;
import climbing.climbBack.climbData.repository.ClimbDataRepository;
import climbing.climbBack.sensorData.domain.SensorData;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ClimbDataService {

    private final ClimbDataRepository climbDataRepository;

    @Transactional
    public Long createClimbingData(List<SensorData> sensorDataList, boolean success) {
        ClimbData climbData = new ClimbData();
        Long routeId = sensorDataList.get(0).getRouteId();

        climbData.setRouteId(routeId);
        climbData.setClimbTime(timeSensing(sensorDataList));
        climbData.setCreatedTime(LocalDateTime.now());
        climbData.setSuccess(true);

        // 연관 관계 편의 매서드 사용 -> SensorData 와 ClimbData 연결
        sensorDataList.forEach(climbData::addSensorData);

        ClimbData saveData = climbDataRepository.save(climbData);
        return saveData.getId();
    }

    private Long timeSensing(List<SensorData> sensorDataList) {
        LocalDateTime firstTime = sensorDataList.get(0).getCreatedTime();
        LocalDateTime lastTime = sensorDataList.get(sensorDataList.size() - 1).getCreatedTime();

        Duration duration = Duration.between(firstTime, lastTime);
        return duration.getSeconds();
    }
}
