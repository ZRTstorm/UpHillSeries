package climbing.climbBack.user.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter @Setter
public class Users {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    // Firebase 에서 발급한 사용자의 고유한 ID
    private String uid;

    private String email;

    // User Nickname
    private String nickname;

    @Enumerated(EnumType.STRING)
    private UserRole role;
}
