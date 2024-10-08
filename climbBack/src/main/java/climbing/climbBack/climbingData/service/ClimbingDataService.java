package climbing.climbBack.climbingData.service;

import climbing.climbBack.climbingData.domain.ClimbingData;
import climbing.climbBack.climbingData.repository.ClimbingDataRepository;
import climbing.climbBack.sensor.service.SensorService;
import climbing.climbBack.sensorData.domain.SensorData;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ClimbingDataService {

    private final ClimbingDataRepository climbingDataRepository;
    private final SensorService sensorService;

    // 등반 기록 임시 저장소
    // key : routeId    value : ClimbingData
    ConcurrentHashMap<Long, ClimbingData> climbingDataMap = new ConcurrentHashMap<>();

    // 등반 시작 -> 등반 기록 생성
    public void createClimbingData(Long sensorId) {
        ClimbingData climbingData = new ClimbingData();

        // 시작 신호를 보낸 센서 ID 로부터 route ID 탐색
        Long routeId = sensorService.getRouteBySensor(sensorId);
        climbingData.setRouteId(routeId);

        // 재시작 하는 경우 임시 저장소 이전 기록 삭제
        climbingDataMap.remove(routeId);

        // 등반 시작 시각 기록
        climbingData.setIsCreated(LocalDateTime.now());

        // route 를 등반 중인 user 탐색 -> 기록
        // EntryQueueService 의 routeId - userId 로부터 획득

        // 등반 기록 임시 저장소 저장
        climbingDataMap.put(routeId, climbingData);
    }

    // 등반 성공 기록 저장 서비스
    public void successClimbingData(Long sensorId) {
        Long routeId = sensorService.getRouteBySensor(sensorId);

        // 임시 저장소 데이터 불러 오기 & 삭제
        ClimbingData climbingData = climbingDataMap.remove(routeId);

        // 등반 기록 성공 처리
        climbingData.setSuccess(true);

        // 등반 시간 계산
        Long duringTime = calculateDateToTime(climbingData);
        climbingData.setClimbingTime(duringTime);

        // 등반 성공 기록 저장
        climbingDataRepository.save(climbingData);
    }

    // Data 를 보낸 센서의 루트 에서 현재 사용 중인 유저가 있는지 확인
    // EntryQueueService 의 routeId - userId 로부터 획득
    public boolean checkUserInRoute(Long sensorId) {
        Long routeId = sensorService.getRouteBySensor(sensorId);

        return true;
    }

    private Long calculateDateToTime(ClimbingData climbingData) {
        LocalDateTime firstTime = climbingData.getIsCreated();
        LocalDateTime lastTime = LocalDateTime.now();

        Duration duration = Duration.between(firstTime, lastTime);
        return duration.getSeconds();
    }
}
