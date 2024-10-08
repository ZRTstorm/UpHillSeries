package climbing.climbBack.route.domain;

import jakarta.persistence.*;

@Entity
public class Route {

    @Id @GeneratedValue
    @Column(name = "route_id")
    private Long id;

    // Route 에 배정된 난이도
    @Enumerated(EnumType.STRING)
    private Difficulty difficulty;
}
