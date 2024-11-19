package climbing.climbBack.battleRoom.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
public class BattleDataDto {

    private Long climbingDataId;

    private String userName;

    private Boolean success;

    private Long climbingTime;
}
