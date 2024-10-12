package climbing.climbBack.entryQueue.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter @Setter
public class EntryQueue {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "entry_id")
    private Long id;

    // EntryQueue 는 객체 그래프 탐색을 하지 않음
    // 대기열 신청한 루트 ID
    @Column(name = "route_id")
    private Long routeId;

    // 대기열 신청한 User ID
    @Column(name = "user_id")
    private Long userId;

    // 루트 대기 순번 -> 전체 대기 순번 과는 다름
    private Long position;

    // 대기열 신청 시각
    private LocalDateTime createdTime;
}
