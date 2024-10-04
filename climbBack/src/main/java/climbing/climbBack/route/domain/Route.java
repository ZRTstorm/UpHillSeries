package climbing.climbBack.route.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

@Entity
public class Route {

    @Id @GeneratedValue
    @Column(name = "route_id")
    private Long id;

    // 루트 번호와 매칭 되는 모든 스탯 Data 를 가져 오는 것은 불가능 -> 참조 X
}
