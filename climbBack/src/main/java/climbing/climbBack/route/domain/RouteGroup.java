package climbing.climbBack.route.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter @Setter
public class RouteGroup {

    @Id @GeneratedValue
    private Long id;

    @Column(name = "route_id1")
    private Long route1;

    @Column(name = "route_id2")
    private Long route2;
}
