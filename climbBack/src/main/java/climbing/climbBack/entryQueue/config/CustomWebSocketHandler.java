package climbing.climbBack.entryQueue.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@Slf4j
public class CustomWebSocketHandler extends TextWebSocketHandler {

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        // Client 와 연결이 수립되었을 때
        log.info("Websocket Connection is Completed : session = {}", session.getId());

        session.sendMessage(new TextMessage("WebSocket Connection is Completed"));
    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        // Client 로부터 메시지를 수신했을 때
        log.info("Client send Message = {}", message.getPayload());

        // 메시지 전송
        session.sendMessage(new TextMessage("Server receive Message = " + message.getPayload()));
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        // Client 와 연결이 종료되었을 때
        log.info("Websocket Connection is over : session = {}", session.getId());
    }
}
