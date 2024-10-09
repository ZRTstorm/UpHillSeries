package climbing.climbBack.climbingData.domain;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class SensorDataDto {

    // Data 를 보낸 센서 ID
    private Long sensorId;

    // true : 홀드를 잡은 경우
    // false : 홀드를 뗀 경우
    private boolean isTouched;
}
