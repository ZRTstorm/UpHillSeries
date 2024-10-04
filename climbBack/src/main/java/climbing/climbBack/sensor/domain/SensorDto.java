package climbing.climbBack.sensor.domain;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class SensorDto {

    @Min(value = 1)
    private Long id;

    @NotNull
    private String center;
    @NotNull
    private String route;

    // 만약 센서가 부착 되는 위치의 좌표 값을 저장 한다면 (x,y,z) Double 값 추가
    // Climbing data -> 등반 패턴 -> image 시각적 처리
    // 만약 image 시각화 만을 위해서 위치 값이 필요 하다면 image 영역 에서만 좌표 처리를 하는게 좋지 않나
}
