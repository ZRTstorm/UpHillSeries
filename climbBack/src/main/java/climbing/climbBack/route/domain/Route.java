package climbing.climbBack.route.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter @Setter
public class Route {

    @Id
    @Column(name = "route_id")
    private Long id;

    // Route 에 배정된 난이도
    @Enumerated(EnumType.STRING)
    private Difficulty difficulty;

    // Route 이미지
    @Lob
    @Basic(fetch = FetchType.LAZY)
    @Column(columnDefinition = "MEDIUMTEXT")
    private String imageData;

    // 루트가 위치한 Climbing Center
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "climbing_center_id")
    private ClimbingCenter climbingCenter;

    // 루트 x 좌표 & y 좌표
    private Integer startX;
    private Integer startY;
    private Integer endX;
    private Integer endY;
}
