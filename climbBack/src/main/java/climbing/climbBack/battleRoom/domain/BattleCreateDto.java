package climbing.climbBack.battleRoom.domain;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class BattleCreateDto {

    @NotBlank
    private String title;

    // 선택
    private String content;

    @NotNull
    private Long routeId;

    // 방장 user ID 는 PathVariable

    // Battle 진행 여부는 True 로 시작

    @NotNull
    private Boolean crewOpen;

    // 입장 코드는 서버에서 생성

}
