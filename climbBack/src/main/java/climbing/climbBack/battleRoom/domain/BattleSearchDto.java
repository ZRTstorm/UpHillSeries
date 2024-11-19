package climbing.climbBack.battleRoom.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
public class BattleSearchDto {

    // BattleRoom ID
    private Long battleRoomId;

    // BattleRoom Title
    private String title;

    // BattleRoom Admin
    private String adminName;

    // BattleRoom Route
    private Long routeId;

    // Battle 이 진행 중인지
    private Boolean Progress;
}
