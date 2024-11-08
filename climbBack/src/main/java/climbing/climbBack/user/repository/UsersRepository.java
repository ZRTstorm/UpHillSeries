package climbing.climbBack.user.repository;

import climbing.climbBack.user.domain.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsersRepository extends JpaRepository<Users, Long> {

    // Firebase UID 를 받아서 Users 반환
    Optional<Users> findByUid(String uid);
}
