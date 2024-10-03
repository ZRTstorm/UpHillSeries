package climbing.climbBack.climbData.repository;

import climbing.climbBack.climbData.domain.ClimbData;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClimbDataRepository extends JpaRepository<ClimbData, Long> {

}
