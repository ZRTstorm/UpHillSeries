package climbing.climbBack.battleRoom.domain;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter @Setter
public class BattleInfoDto {

    private Long battleRoomId;

    private String title;
    private String content;
    private String participantCode;

    private Long adminId;
    private String adminName;

    private Long routeId;
    private Long crewId;

    private Boolean progress;
    private LocalDateTime startTime;

    private List<ParticipantDto> participantList = new ArrayList<>();
}
