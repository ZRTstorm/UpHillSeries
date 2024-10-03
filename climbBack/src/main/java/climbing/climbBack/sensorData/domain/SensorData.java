package climbing.climbBack.sensorData.domain;

import climbing.climbBack.climbData.domain.ClimbData;
import climbing.climbBack.sensor.domain.Sensor;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter @Setter
public class SensorData {

    @Id @GeneratedValue
    private Long id;

    private Long sensorId;
    private Long routeId;

    private boolean isTouched;
    private LocalDateTime createdTime;

    // 등반 기록
    @ManyToOne
    @JoinColumn(name = "climbdata_id")
    private ClimbData climbData;
}
