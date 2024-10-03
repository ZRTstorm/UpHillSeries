package climbing.climbBack.sensorData.domain;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class SensorDataDto {

    private Long routeId;
    private boolean isTouched;
}
