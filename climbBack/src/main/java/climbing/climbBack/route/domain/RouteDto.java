package climbing.climbBack.route.domain;

import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class RouteDto {

    // 수동으로 루트 번호 등록
    @Min(value = 1)
    private Long routeId;

    // 루트 난이도
    private Difficulty difficulty;

    // 루트가 위치한 암장 번호
    private Long climbingCenterId;

    // 루트 x 좌표 & y 좌표
    private Integer startX;
    private Integer startY;
    private Integer endX;
    private Integer endY;
}
