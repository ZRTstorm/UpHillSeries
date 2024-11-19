package climbing.climbBack.battleRoom.domain;

import climbing.climbBack.user.domain.Users;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter @Setter
public class CrewMan {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "crew_man_id")
    private Long id;

    // 가입된 Crew
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "crew_id")
    private Crew crew;

    // 크루원 User
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private Users users;
}
