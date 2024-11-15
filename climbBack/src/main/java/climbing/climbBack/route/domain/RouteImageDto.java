package climbing.climbBack.route.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class RouteImageDto {

    // 루트 이미지 데이터
    private String imageData;

    // 루트 시작 좌표 & 끝 좌표
    private Integer startX;
    private Integer startY;
    private Integer endX;
    private Integer endY;
}
