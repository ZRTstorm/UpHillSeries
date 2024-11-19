package climbing.climbBack.climbingData.domain;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BodyMovementDto {

    private Long sequence;
    private Integer xPos;
    private Integer yPos;
}
