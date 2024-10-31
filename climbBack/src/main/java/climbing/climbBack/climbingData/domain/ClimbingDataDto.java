package climbing.climbBack.climbingData.domain;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter @Setter
public class ClimbingDataDto {

    // ClimbingData ID
    private Long id;

    // Users & Route -> 참조에서 ID 로 변환 : Lazy Loading 주의
    private Long userId;
    private Long routeId;

    private Boolean success;
    private Long climbingTime;
    private LocalDateTime createdTime;

    public ClimbingDataDto(Long id, Long userId, Long routeId, Boolean success, Long climbingTime, LocalDateTime createdTime) {
        this.id = id;
        this.userId = userId;
        this.routeId = routeId;
        this.success = success;
        this.climbingTime = climbingTime;
        this.createdTime = createdTime;
    }
}
