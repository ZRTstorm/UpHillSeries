package climbing.climbBack.climbingData.repository;

import climbing.climbBack.climbingData.domain.ClimbingData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ClimbingDataRepository extends JpaRepository<ClimbingData, Long> {

    // 모든 등반 기록 조회 Query
    @Query("select cd from ClimbingData cd " +
            "join fetch cd.users u join fetch cd.route r")
    List<ClimbingData> findAllFetch();

    // 사용자의 모든 등반 기록 조회 Query
    @Query("select cd from ClimbingData cd " +
            "join fetch cd.users u join fetch cd.route r " +
            "where u.id = :userId")
    List<ClimbingData> findAllByUserId(@Param("userId") Long userId);

    // userId 와 일치 하는 Data 중 가장 id 값이 높은 Data 조회 Query
    @Query("select cd from ClimbingData cd " +
            "where cd.users.id = :userId order by cd.id desc")
    Optional<ClimbingData> findTopByUserId(@Param("userId") Long userId);
}
