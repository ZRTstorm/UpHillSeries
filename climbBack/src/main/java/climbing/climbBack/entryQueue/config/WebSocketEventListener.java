package climbing.climbBack.entryQueue.config;

import climbing.climbBack.entryQueue.domain.EntryCountDto;
import climbing.climbBack.entryQueue.service.EntryQueueService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Component
@RequiredArgsConstructor
public class WebSocketEventListener {

    private final EntryQueueService entryQueueService;

    // Client 와 WebSocket 연결 Listener
    // App 에서 로그인 할 때 , Websocket 연결
    @EventListener
    public void handleWebSocketConnectListener(SessionConnectEvent event) {
        // SessionId 추출
        StompHeaderAccessor headers = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = headers.getSessionId();

        // userId 는 WebSocket 연결 시 헤더에 직접 담아서 전송 해야 한다
        // { userId : xxx } 형식
        Long userId = getUserIdByHeader(headers);

        // userId - SessionId 저장
        entryQueueService.saveUserSession(userId, sessionId);
    }

    // Client 와 WebSocket 해제 Listener
    // Server 에서 직접 해제는 하지 않음 -> Client 에서 해제 요청만 Listen
    // App 을 종료할 때 한 번 disconnect
    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor headers = StompHeaderAccessor.wrap(event.getMessage());

        // header -> userId 추출
        Long userId = getUserIdByHeader(headers);

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
    }

    // 헤더 에서 userID 추출
    private Long getUserIdByHeader(StompHeaderAccessor headers) {
        String userId = headers.getFirstNativeHeader("userId");
        assert userId != null;
        return Long.valueOf(userId);
    }
}
