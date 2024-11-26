package climbing.climbBack.battleRoom.domain;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class CrewManRegisterDto {

    // 가입 신청 Crew ID
    private Long crewId;

    // 가입 신청 password
    private String password;
}
