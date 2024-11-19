package climbing.climbBack.battleRoom.domain;


import climbing.climbBack.route.domain.Route;
import climbing.climbBack.user.domain.Users;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter @Setter
public class BattleRoom {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "battle_room_id")
    private Long id;

    // Battle 제목 & 내용
    private String title;
    private String content;

    // Battle 진행 루트
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "route_id")
    private Route route;

    // BattleRoom 을 생성한 User -> Admin
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private Users adminUser;

    // Battle 진행 여부
    private Boolean progress;

    // Crew 에게 오픈 할지 여부 ( Battle 설정 )
    // Open 하지 X : 0 , Open O : Long crewId
    private Long crewId;

    // BattleRoom 입장 코드
    private String participantCode;

    // Battle 시작 시각
    private LocalDateTime startTime;
}
