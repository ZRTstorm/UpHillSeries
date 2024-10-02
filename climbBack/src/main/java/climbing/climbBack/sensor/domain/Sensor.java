package climbing.climbBack.sensor.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter @Setter
public class Sensor {

    @Id
    @Column(name = "sensor_id")
    private Long id;

    private String center;
    private String route;
}
