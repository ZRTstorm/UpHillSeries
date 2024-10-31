package climbing.climbBack.climbingData.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter @Setter
public class BodyMovement {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "body_movement_id")
    private Long id;

    // 위치 데이터가 속한 ClimbingData ID
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "climbing_data_id")
    private ClimbingData climbingData;

    // 등반 패턴에서 위치 데이터의 순서
    private Long sequence;

    // Object 의 위치 (x,y)
    private Integer xPos;
    private Integer yPos;
}
