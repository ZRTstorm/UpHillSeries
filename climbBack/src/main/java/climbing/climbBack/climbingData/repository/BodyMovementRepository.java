package climbing.climbBack.climbingData.repository;

import climbing.climbBack.climbingData.domain.BodyMovement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BodyMovementRepository extends JpaRepository<BodyMovement, Long> {

    // climbingDataId 와 Mapping 되는 Movement Data 가 존재 하는지 조회
    boolean existsByClimbingDataId(Long climbingDataId);

    // climbingDataId 와 Mapping 되는 모든 Movement Data 조회
    List<BodyMovement> findByClimbingDataId(Long climbingDataId);

    // climbingData 와 연관된 모든 BodyMovement Data 삭제 Query
    @Modifying
    @Query("delete from BodyMovement bm " +
            "where bm.climbingData.id = :climbingDataId")
    void deleteAllByClimbingDataId(@Param("climbingDataId") Long climbingDataId);
}
