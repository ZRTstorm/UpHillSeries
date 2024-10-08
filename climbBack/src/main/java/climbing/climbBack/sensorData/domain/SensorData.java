package climbing.climbBack.sensorData.domain;

import climbing.climbBack.climbingData.domain.ClimbingData;
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

    @ManyToOne
    @JoinColumn(name = "sensor_id")
    private Sensor sensor;
    private Long routeId;

    private boolean isTouched;
    private LocalDateTime createdTime;

    // 등반 기록
    @ManyToOne
    @JoinColumn(name = "climb_data_id")
    private ClimbingData climbingData;
}
