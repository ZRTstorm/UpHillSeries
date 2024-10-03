package climbing.climbBack.climbData.domain;

import climbing.climbBack.sensorData.domain.SensorData;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
public class ClimbData {

    @Id @GeneratedValue
    @Column(name = "climbdata_id")
    private Long id;

    private Long userId;
    private Long routeId;

    private boolean success;
    private Long climbTime;

    @OneToMany(mappedBy = "climbData", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SensorData> sensorList = new ArrayList<>();

    private LocalDateTime createdTime;

    public void addSensorData(SensorData sensorData) {
        sensorList.add(sensorData);
        sensorData.setClimbData(this);
    }

    public void removeSensorData(SensorData sensorData) {
        sensorList.remove(sensorData);
        sensorData.setClimbData(null);
    }
}
