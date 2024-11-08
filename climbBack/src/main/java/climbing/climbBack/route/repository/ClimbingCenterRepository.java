package climbing.climbBack.route.repository;

import climbing.climbBack.route.domain.ClimbingCenter;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClimbingCenterRepository extends JpaRepository<ClimbingCenter, Long> {
}
