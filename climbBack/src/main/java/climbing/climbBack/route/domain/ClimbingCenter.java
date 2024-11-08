package climbing.climbBack.route.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter @Setter
public class ClimbingCenter {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "climbing_center_id")
    private Long id;

    // Climbing Center 이름
    private String centerName;

    // center 이미지
    @Lob
    @Basic(fetch = FetchType.LAZY)
    @Column(columnDefinition = "MEDIUMTEXT")
    private String imageData;
}
