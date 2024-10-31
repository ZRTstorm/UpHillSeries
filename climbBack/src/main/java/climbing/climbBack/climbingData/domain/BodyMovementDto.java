package climbing.climbBack.climbingData.domain;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class BodyMovementDto {

    private Long sequence;
    private Integer xPos;
    private Integer yPos;

    public BodyMovementDto(Long sequence, Integer xPos, Integer yPos) {
        this.sequence = sequence;
        this.xPos = xPos;
        this.yPos = yPos;
    }
}
