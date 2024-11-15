package climbing.climbBack.entryQueue.config;

import climbing.climbBack.entryQueue.domain.EntryCountDto;
import climbing.climbBack.entryQueue.service.EntryQueueService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;

@Component
@RequiredArgsConstructor
@Slf4j
public class WebSocketEventListener {

    private final EntryQueueService entryQueueService;
    private final SimpMessagingTemplate messagingTemplate;

    // Client 와 WebSocket 연결 Listener
    // App 에서 로그인 할 때 , Websocket 연결
    @EventListener
    public void handleWebSocketConnectListener(SessionConnectEvent event) {
        // Connection Log
        log.info("WebSocket connection is Started");

        // SessionId 추출
        StompHeaderAccessor headers = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = headers.getSessionId();

        // userId 는 WebSocket 연결 시 헤더에 직접 담아서 전송 해야 한다
        // { userId : xxx } 형식
        Long userId = (Long) headers.getSessionAttributes().get("userId");

        if (userId == null) {
            log.warn("userId is not in Parameter.");
            return;
        }

        // userId - SessionId 저장
        entryQueueService.saveUserSession(userId, sessionId);

        // 기본 구독 경로로 sessionId 전송
        // Client 구독 방식
    }

    // Client 와 WebSocket 해제 Listener
    // Server 에서 직접 해제는 하지 않음 -> Client 에서 해제 요청만 Listen
    // App 을 종료할 때 한 번 disconnect
    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor headers = StompHeaderAccessor.wrap(event.getMessage());

        // header -> userId 추출
        Long userId = (Long) headers.getSessionAttributes().get("userId");

        if (userId == null) {
            userId = entryQueueService.getUserIdBySessionId(headers.getSessionId());
        }

        // UserId - SessionId 저장소 에서 sessionId 삭제
        entryQueueService.deleteUserSession(userId);

        // User 가 아직 EntryQueue Data 를 가지고 있는지 검사
        if (entryQueueService.checkQueueForUser(userId)) {
            // { routeId : user 의 Position }
            EntryCountDto entryCount = entryQueueService.getUserEntryCountOne(userId);

            // 이용 중인 User 인지 검사
            if (entryCount.getCount() == 0L) {
                entryQueueService.manipulateEntryQueue(entryCount.getRouteId());
            } else {
                entryQueueService.deleteEntry(userId);
            }
        }

        log.info("WebSocket Disconnected Successfully");
    }

    // 구독 Event Listener
    @EventListener
    public void handleSessionSubscribeEvent(SessionSubscribeEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());

        if (accessor.getCommand() == StompCommand.SUBSCRIBE) {
            // 구독한 경로
            String destination = accessor.getDestination();
            log.info("User subscribe in destination = {}", destination);

            assert destination != null;
            messagingTemplate.convertAndSend(destination, "User subscribe in destination");
        }
    }

    // 헤더 에서 userID 추출
    // Header 에 userId 가 없는 경우 null 반환
    private Long getUserIdByHeader(StompHeaderAccessor headers) {
        String userId = headers.getFirstNativeHeader("userId");

        if (userId == null) return null;
        else return Long.valueOf(userId);
    }
}
