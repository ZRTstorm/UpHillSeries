package climbing.climbBack.climbingData.domain;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter @Setter
public class MovementOutputDto {

    // 이미지 파일 URL
    private String imageUrl;

    // 시작 홀드와 탑 홀드의 좌표 값
    private Integer startX;
    private Integer startY;
    private Integer endX;
    private Integer endY;

    // 좌표 리스트
    private List<BodyMovementDto> movements;
}
