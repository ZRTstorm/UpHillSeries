package climbing.climbBack.route.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@AllArgsConstructor
public class RouteGetDto {

    private Long routeId;

    // Route 난이도
    private Difficulty difficulty;

    // Route 의 Center ID
    private Long centerId;

}
