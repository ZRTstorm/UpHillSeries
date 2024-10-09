package climbing.climbBack.sensor.domain;

import climbing.climbBack.route.domain.Route;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter @Setter
public class Sensor {

    @Id
    @Column(name = "sensor_id")
    private Long id;

    // sensor 가 부착된 route
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "route_id")
    private Route route;
}
