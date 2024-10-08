package climbing.climbBack.entryQueue.domain;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class QueueRegisterDto {

    // 대기열 등록 유저 ID
    private Long userId;

    // 대기열 등록 루트 ID
    private Long routeId;
}
