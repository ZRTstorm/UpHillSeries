package climbing.climbBack.climbingData.domain;

import climbing.climbBack.route.domain.Route;
import climbing.climbBack.user.domain.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter @Setter
public class ClimbingData {

    @Id @GeneratedValue
    @Column(name = "climbing_data_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "route_id")
    private Route route;

    // 등반 성공 여부
    private Boolean success;

    // 등반 소요 시간 (sec)
    private Long climbingTime;

    // 등반 기록 생성 시각
    private LocalDateTime createdTime;
}
