package climbing.climbBack.battleRoom.domain;

import climbing.climbBack.user.domain.Users;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter @Setter
public class Crew {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "crew_id")
    private Long id;

    // Crew 이름 & 소개
    private String crewName;
    private String content;

    // 크루 Pilot
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private Users adminUser;

    // 크루 아이콘 Image
    private String crewIcon;
}
