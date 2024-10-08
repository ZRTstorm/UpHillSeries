package climbing.climbBack.climbingData.domain;

import climbing.climbBack.sensorData.domain.SensorData;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
public class ClimbingData {

    @Id @GeneratedValue
    @Column(name = "climbing_data_id")
    private Long id;

    // id vs reference 고민 필요
    @Column(name = "user_id")
    private Long userId;

    // id vs reference 고민 필요
    @Column(name = "route_id")
    private Long routeId;

    // 등반 성공 여부
    private boolean success;

    // 등반 소요 시간
    private Long climbingTime;

    // 등반 기록 생성 시각
    private LocalDateTime isCreated;
}
