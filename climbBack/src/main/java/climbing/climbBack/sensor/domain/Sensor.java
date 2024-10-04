package climbing.climbBack.sensor.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter @Setter
public class Sensor {

    @Id
    @Column(name = "sensor_id")
    private Long id;

    private Long centerId;
    private Long routeId;
}
