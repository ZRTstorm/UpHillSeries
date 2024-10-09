package climbing.climbBack.route.domain;

import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class RouteDto {

    @Min(value = 1)
    private Long routeId;

    private Difficulty difficulty;
}
