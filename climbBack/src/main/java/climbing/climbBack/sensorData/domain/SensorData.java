package climbing.climbBack.sensorData.domain;

import climbing.climbBack.sensor.domain.Sensor;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter @Setter
public class SensorData {

    @Id @GeneratedValue
    Long id;

    @ManyToOne
    @JoinColumn(name = "sensor_id")
    private Sensor sensor;
    private String routeId;

    boolean isTouched;
    LocalDateTime createdTime;

    // 등반 기록 번호 -> 참조 혹은 번호
}
