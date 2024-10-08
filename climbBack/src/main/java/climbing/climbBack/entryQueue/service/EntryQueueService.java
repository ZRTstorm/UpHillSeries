package climbing.climbBack.entryQueue.service;

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


    // 대기열 정보 생성 서비스
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

        // 대기열 등록 후 바로 이용 가능 한지 검사
        // 이전 대기 유저가 존재 하면 바로 이용 불가
        if (lastEntry.isPresent() || routeUserMap.containsKey(routeId)) return;

        // 등록 루트와 연관된 루트 List 확인
        List<Long> routeGroupList = routeGroupService.getGroupById(routeId);

        // 간섭 루트 중 사용 중인 루트가 존재 하는지 확인 -> 없다면 사용 가능
        for (Long inferRoute : routeGroupList) {
            if (routeUserMap.containsKey(inferRoute)) return;
        }

        // 이용 허가
        // Route - User 정보 저장 -> 이용자 기록 저장
        routeUserMap.put(routeId, userId);
        // 대기열 pos 조정
        entryQueueRepository.decreasePositionByRouteId(routeId);

        // WebSocket 으로 이용 허가 message 전송
        notifyToUser(userId, "allowToUseRoute");
    }

    // 등록된 대기열 삭제 서비스
    @Transactional
    public void deleteEntry(Long userId) {
        // User 대기열 데이터 삭제
        entryQueueRepository.deleteAllByUserId(userId);
    }

    // 이미 유저가 대기열 등록을 했는지 검사 -> 대기열 중복 등록은 허용 하지 않음
    @Transactional(readOnly = true)
    public boolean checkQueueForUser(Long userId) {
        Optional<EntryQueue> entryQueue = entryQueueRepository.findByUserId(userId);

        // Data 가 존재 한다면 true , 존재 하지 않는 다면 false return
        return entryQueue.isPresent();
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
