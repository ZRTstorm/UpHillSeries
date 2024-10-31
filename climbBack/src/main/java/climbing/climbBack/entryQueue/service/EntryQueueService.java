package climbing.climbBack.entryQueue.service;

import climbing.climbBack.entryQueue.domain.EntryCountDto;
import climbing.climbBack.entryQueue.domain.EntryQueue;
import climbing.climbBack.entryQueue.repository.EntryQueueRepository;
import climbing.climbBack.route.service.RouteGroupService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
@Slf4j
public class EntryQueueService {

    private final EntryQueueRepository entryQueueRepository;
    private final RouteGroupService routeGroupService;
    private final SimpMessagingTemplate messagingTemplate;

    // UserID - WebSocket SessionID 저장소
    // key : userId    value : sessionId
    private final ConcurrentHashMap<Long, String> userSessionMap = new ConcurrentHashMap<>();

    // RouteId - UserId 저장소 -> route 를 사용 하는 이용자 임시 기록
    // key : routeId    value : userId
    private final ConcurrentHashMap<Long, Long> routeUserMap = new ConcurrentHashMap<>();

    // User - SessionId 저장 서비스
    public void saveUserSession(Long userId, String sessionId) {
        // Duplication 확인 -> value Update
        if (userSessionMap.containsKey(userId)) {
            log.info("WebSocket connection duplication = {}", userId);
        }

        userSessionMap.put(userId, sessionId);
    }

    // User - SessionId 삭제 서비스
    public void deleteUserSession(Long userId) {
        userSessionMap.remove(userId);
    }


    // 대기열 Data 생성 서비스
    @Transactional
    public void createEntryQueue(Long userId, Long routeId) {
        EntryQueue entryQueue = new EntryQueue();

        // userId & routeId 기록
        entryQueue.setUserId(userId);
        entryQueue.setRouteId(routeId);

        // 대기열 등록 시각 기록
        entryQueue.setCreatedTime(LocalDateTime.now());

        // 대기열 루트 별 대기 순번 기록
        // 대기열 DB 에서 같은 route 에 대기 하는 순번 중 가장 마지막 순번을 가져 와서 추가
        Optional<EntryQueue> lastEntry = entryQueueRepository.findFirstByRouteIdOrderByPositionDesc(routeId);
        if (lastEntry.isEmpty()) {
            entryQueue.setPosition(1L);
        } else {
            entryQueue.setPosition(lastEntry.get().getPosition() + 1L);
        }

        // 대기열 엔티티 저장
        entryQueueRepository.save(entryQueue);

        // 현재 대기열이 바로 이용 가능 한지 검사
        // 이전 대기 유저가 존재 하면 이용 불가
        if (lastEntry.isPresent() || routeUserMap.containsKey(routeId)) return;

        // 등록 루트와 연관된 루트 List 조회
        List<Long> routeGroupList = routeGroupService.getGroupById(routeId);

        // 연관된 루트 중 사용 중인 루트가 존재 하는지 확인
        // 연관된 모든 루트가 비어 있다면 이용 가능
        for (Long inferRoute : routeGroupList) {
            if (routeUserMap.containsKey(inferRoute)) return;
        }

        // 이용 허가
        // Route - User 정보 저장 -> 해당 루트의 이용자를 임시 저장
        routeUserMap.put(routeId, userId);

        // 대기열 pos 조정
        entryQueueRepository.decreasePositionByRouteId(routeId);

        // WebSocket 으로 이용 허가 message 전송
        notifyToUser(userId, "allowToUseRoute");
    }

    // 등록된 대기열 삭제 서비스 -> User 가 임의로 열을 벗어 나는 경우
    @Transactional
    public void deleteEntry(Long userId) {
        // 삭제 하는 Data 조회
        Optional<EntryQueue> deleteData = entryQueueRepository.findByUserId(userId);
        if (deleteData.isEmpty()) {
            throw new IllegalStateException("User did not register entry : userID = " + userId);
        }

        // 삭제 하는 Data 보다 후순위 Data 의 position 값을 조정
        entryQueueRepository.decreasePositionGreater(deleteData.get().getRouteId(), deleteData.get().getPosition());

        // User 대기열 데이터 삭제
        entryQueueRepository.deleteAllByUserId(userId);
    }

    // 요청한 유저가 이미 대기열을 가지고 있는지 검사
    // Rule : 대기열은 모든 루트에 대하여 중복 등록 할 수 없다
    @Transactional(readOnly = true)
    public boolean checkQueueForUser(Long userId) {
        Optional<EntryQueue> entryQueue = entryQueueRepository.findByUserId(userId);

        // Data 가 존재 한다면 true , 존재 하지 않는 다면 false return
        return entryQueue.isPresent();
    }

    // Route 이용 하는 User 를 변경 -> 대기열 조정 서비스
    // 등반 완료를 기점 으로 자동 실행 하는 서비스
    @Transactional
    public void manipulateEntryQueue(Long routeId) {
        // route 를 이용한 User 의 route User Data 삭제 & userId 획득
        Long userId = routeUserMap.remove(routeId);

        // 완료 User 의 EntryQueue Data 삭제 -> pos == 0 Data
        entryQueueRepository.deleteAllByUserId(userId);

        // 종료한 route 를 기준 으로 이용 할 수 있는 route 대기자 탐색
        // route 에서 간섭 관계를 가진 route List 를 획득
        List<Long> routeList = routeGroupService.getGroupById(routeId);

        // routeList 에 현재 route 추가
        routeList.add(routeId);

        // route List 에 포함된 route 들 중 현재 pos == 1 인 Data 중
        // createdTime 이 빠른 순서 대로 정렬 해서 EntryQueue 획득
        // 빈 List 일 경우 -> 대기 인원이 존재 하지 않음 -> 아무 일도 일어 나지 않음
        List<EntryQueue> entryList = entryQueueRepository.findEntryListOrderedByCreatedTime(routeList);

        // 순서 대로 확인 하여 이용 가능 한지 확인
        for (EntryQueue entry : entryList) {
            // 간섭 관계를 가진 route List 획득
            List<Long> checkList = routeGroupService.getGroupById(entry.getRouteId());
            checkList.add(entry.getRouteId());

            // 간섭 관계를 가진 route 를 누군가 이용 중인지 확인
            boolean isInfer = false;
            for (Long checkRoute : checkList) {
                if (routeUserMap.containsKey(checkRoute)) {
                    isInfer = true;
                    break;
                }
            }

            // isInfer == true : 이용 중인 유저가 존재
            // isInfer == false : 이용 중인 유저가 없어서 이용 가능
            if (isInfer) continue;
            else {
                // Route - User 이용자 기록
                Long ableRouteId = entry.getRouteId();
                Long ableUserId = entry.getUserId();
                routeUserMap.put(ableRouteId, ableUserId);

                // 이용할 routeId 의 모든 pos 값 1씩 감소
                entryQueueRepository.decreasePositionByRouteId(ableRouteId);

                // WebSocket 으로 이용 허가 message 전송
                notifyToUser(ableUserId, "allowToUseRoute");

                break;
            }
        }
    }

    // 모든 대기열 Data 조회 서비스
    @Transactional(readOnly = true)
    public List<EntryQueue> getAllEntryData() {
        return entryQueueRepository.findAll();
    }

    // 모든 route 별 대기열 COUNT 조회 서비스
    @Transactional(readOnly = true)
    public List<EntryCountDto> getAllEntryCountList() {
        return entryQueueRepository.countAllEntryByRoute();
    }

    // 특정 route 의 대기 인원 조회 서비스
    @Transactional(readOnly = true)
    public EntryCountDto getRouteEntryCountOne(Long routeId) {
        // { routeId : COUNT } 형태로 조회
        return entryQueueRepository.countRouteEntry(routeId)
                .orElse(new EntryCountDto(routeId, 0L));
    }

    // 사용자의 routeId : COUNT 위치 정보 조회 서비스
    @Transactional(readOnly = true)
    public EntryCountDto getUserEntryCountOne(Long userId) {
        // { routeId : user 의 position } 형태로 조회
        return entryQueueRepository.findPositionRouteByUserId(userId)
                .orElse(new EntryCountDto());
    }

    // userId 와 일치 하는 Data 의 routeId 를 조회 하는 서비스
    @Transactional(readOnly = true)
    public Long getRouteIdByUserId(Long userId) {
        return entryQueueRepository.findRouteIdByUserId(userId);
    }

    // routeId - UserId Data 가 routeUserMap 에 존재 하는지 확인
    // routeId 에 대해 존재 한다면 true, 없다면 false return
    public boolean checkUserByRoute(Long routeId) {
        return routeUserMap.containsKey(routeId);
    }

    // routeId 로부터 value 값 userId 를 획득 서비스
    public Long getUserByRouteMap(Long routeId) {
        return routeUserMap.get(routeId);
    }

    // userId 로부터 key 값 routeId 를 획득 서비스
    public Long getRouteByUserMap(Long userId) {
        for (Map.Entry<Long, Long> entry : routeUserMap.entrySet()) {
            if (entry.getValue().equals(userId)) return entry.getKey();
        }

        log.info("Cannot get routeId from userId in routeUserMap = {}", userId);
        return -1L;
    }

    // User 에게 WebSocket 을 통해 message 전송
    // Destination : /queue/notification
    public void notifyToUser(Long userId, String message) {
        String sessionId = userSessionMap.get(userId);

        if (sessionId != null) {
            messagingTemplate.convertAndSendToUser(sessionId, "/queue/notification", message);
        }
    }
}
