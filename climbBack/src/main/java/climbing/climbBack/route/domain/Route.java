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
}
