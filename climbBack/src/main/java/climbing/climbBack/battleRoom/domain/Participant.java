package climbing.climbBack.battleRoom.domain;

import climbing.climbBack.climbingData.domain.ClimbingData;
import climbing.climbBack.user.domain.Users;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter @Setter
public class Participant {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "participant_id")
    private Long id;

    // Battle 참여 User
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private Users users;

    // BattleRoom
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "battle_room_id")
    private BattleRoom battleRoom;

    // Battle 참여 Climbing 기록
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "climbing_data_id")
    private ClimbingData climbingData;
}
