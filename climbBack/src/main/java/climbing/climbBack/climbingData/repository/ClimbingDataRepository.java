package climbing.climbBack.climbingData.repository;

import climbing.climbBack.climbingData.domain.ClimbingData;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClimbingDataRepository extends JpaRepository<ClimbingData, Long> {

}
