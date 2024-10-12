package climbing.climbBack.route.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter @Setter
public class RouteGroup {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "route_id1")
    private Route route1;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "route_id2")
    private Route route2;
}
