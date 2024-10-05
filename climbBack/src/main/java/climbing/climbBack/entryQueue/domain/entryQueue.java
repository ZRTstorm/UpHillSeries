package climbing.climbBack.entryQueue.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter @Setter
public class entryQueue {

    @Id @GeneratedValue
    @Column(name = "entry_id")
    private Long id;

    // entryQueue 는 대기 열을 DB 로 저장 및 삭제만 하기 때문에 객체 그래프 탐색은 X
    @Column(name = "route_id")
    private Long routeId;

    @Column(name = "user_id")
    private Long userId;

    // WebSocket 통신을 위한 세션 ID
    @Column(name = "session_id")
    private String sessionId;

    // 루트 대기 순번 -> 전체 대기 순번 과는 다름
    private Long position;
    private LocalDateTime createdTime;
}
