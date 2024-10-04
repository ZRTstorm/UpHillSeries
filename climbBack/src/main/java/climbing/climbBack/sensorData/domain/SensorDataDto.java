package climbing.climbBack.sensorData.domain;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class SensorDataDto {

    @NotNull
    private Long routeId;
    private boolean isTouched;
}
