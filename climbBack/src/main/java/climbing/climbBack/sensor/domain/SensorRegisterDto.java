package climbing.climbBack.sensor.domain;

import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class SensorRegisterDto {

    @Min(value = 1)
    private Long sensorId;

    @Min(value = 1)
    private Long routeId;
}
