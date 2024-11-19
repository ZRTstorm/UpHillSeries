package climbing.climbBack.battleRoom.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
public class CrewSearchDto {

    private Long crewId;

    private String crewName;
    private String content;

    private String userName;
}
