package climbing.climbBack.climbingData.repository;

import climbing.climbBack.climbingData.domain.BodyMovement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BodyMovementRepository extends JpaRepository<BodyMovement, Long> {

    // climbingDataId 와 Mapping 되는 Movement Data 가 존재 하는지 조회
    boolean existsByClimbingDataId(Long climbingDataId);

    // climbingDataId 와 Mapping 되는 모든 Movement Data 조회
    List<BodyMovement> findByClimbingDataId(Long climbingDataId);
}
