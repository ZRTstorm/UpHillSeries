package climbing.climbBack.climbingData.service;

import climbing.climbBack.climbingData.domain.ClimbingData;
import climbing.climbBack.climbingData.repository.ClimbingDataRepository;
import climbing.climbBack.entryQueue.service.EntryQueueService;

import climbing.climbBack.route.repository.RouteRepository;
import climbing.climbBack.sensor.service.SensorService;
import climbing.climbBack.user.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;

import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
@Slf4j
public class ClimbingDataService {

    private final ClimbingDataRepository climbingDataRepository;
    private final RouteRepository routeRepository;
    private final UsersRepository usersRepository;

    private final SensorService sensorService;
    private final EntryQueueService entryQueueService;

    // 등반 기록 임시 저장소
    // key : routeId    value : ClimbingData
    ConcurrentHashMap<Long, ClimbingData> climbingDataMap = new ConcurrentHashMap<>();

    // 등반 시작 -> 등반 기록 생성
    public void createClimbingData(Long sensorId) {
        ClimbingData climbingData = new ClimbingData();

        // 시작 신호를 보낸 센서 ID 로부터 route ID 탐색
        Long routeId = sensorService.getRouteBySensor(sensorId);
        climbingData.setRoute(routeRepository.getReferenceById(routeId));

        // 재시작 하는 경우 임시 저장소 이전 기록 삭제
        climbingDataMap.remove(routeId);

        // 등반 시작 시각 기록
        climbingData.setCreatedTime(LocalDateTime.now());

        // route 를 등반 중인 user 탐색 -> 기록
        // EntryQueueService 의 routeId - userId 로부터 획득
        Long userId = entryQueueService.getUserByRouteMap(routeId);

        // user 필드 주입
        climbingData.setUsers(usersRepository.getReferenceById(userId));

        // 등반 기록 임시 저장소 저장
        climbingDataMap.put(routeId, climbingData);
    }

    // 등반 성공 기록 저장 서비스
    @Transactional
    public void successClimbingData(Long sensorId) {
        Long routeId = sensorService.getRouteBySensor(sensorId);

        // 등반 성공 기록 저장
        ClimbingData climbingData = recordClimbingData(routeId, true);

        // 등반에 성공 했음을 Client 에게 알림
        // entryQueueService.notifyToUser(climbingData.getUsers().getId(), "successToClimbing");

        // 대기열 조정 명령
        changeTurnOfUser(routeId);
    }

    // 등반 실패 기록 저장 서비스
    @Transactional
    public void failureClimbingData(Long userId) {
        // userId 로부터 routeId 획득
        Long routeId = entryQueueService.getRouteByUserMap(userId);

        if (routeId == -1L) {
            log.info("User is not playing Climbing = {}", userId);
            throw new IllegalStateException("There are no User data in climbing" + userId);
        }

        // 등반 실패 기록 저장
        recordClimbingData(routeId, false);

        // 대기열 조정 명령
        changeTurnOfUser(routeId);
    }

    // Data 를 보낸 센서의 루트 에서 현재 사용 중인 유저가 있는지 확인
    // EntryQueueService 의 routeId - userId 로부터 획득
    // 사용 하는 유저가 없다면 true, 있다면 false return
    public boolean checkUserInRoute(Long sensorId) {
        Long routeId = sensorService.getRouteBySensor(sensorId);

        // routeUserMap 에서 Data 확인
        return !entryQueueService.checkUserByRoute(routeId);
    }

    // 등반 기록을 불러 와서 남은 필드를 채우고 DB 에 저장
    private ClimbingData recordClimbingData(Long routeId, boolean success) {
        // 임시 저장소 데이터 불러 오기 & 삭제
        ClimbingData climbingData = climbingDataMap.remove(routeId);

        // 등반 기록 성공 & 실패 처리
        climbingData.setSuccess(success);

        // 등반 시간 계산
        Long duringTime = calculateDateToTime(climbingData);
        climbingData.setClimbingTime(duringTime);

        // 등반 성공 & 실패 기록 저장
        climbingDataRepository.save(climbingData);

        return climbingData;
    }

    // 등반의 성공 혹은 실패 -> 등반 완료 판단 후 -> route User 변경
    private void changeTurnOfUser(Long routeId) {
        entryQueueService.manipulateEntryQueue(routeId);
    }

    // 현재 시각 부터 기록 생성 시각 과의 차이를 return
    private Long calculateDateToTime(ClimbingData climbingData) {
        LocalDateTime firstTime = climbingData.getCreatedTime();
        LocalDateTime lastTime = LocalDateTime.now();

        Duration duration = Duration.between(firstTime, lastTime);
        return duration.getSeconds();
    }
}
