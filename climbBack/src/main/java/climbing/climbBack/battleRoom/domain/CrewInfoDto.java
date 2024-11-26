package climbing.climbBack.battleRoom.domain;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter @Setter
public class CrewInfoDto {

    // Crew ID
    private Long crewId;

    // Crew 이름
    private String crewName;

    // Crew 설명
    private String crewContent;

    // Crew Password
    private String password;

    // Crew pilot
    private Long pilotId;
    private String pilotName;

    // crewMan List
    List<CrewManSearchDto> crewManList = new ArrayList<>();
}
