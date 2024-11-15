package climbing.climbBack.route.repository;

import climbing.climbBack.route.domain.CenterGetDto;
import climbing.climbBack.route.domain.ClimbingCenter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ClimbingCenterRepository extends JpaRepository<ClimbingCenter, Long> {

    // 모든 암장 조회 Query ( 이미지 제외 )
    @Query("select new climbing.climbBack.route.domain.CenterGetDto(c.id, c.centerName) " +
            "from ClimbingCenter c")
    List<CenterGetDto> findAllCenters();

    // 암장 이미지 조회 Query
    @Query("select c.imageData " +
            "from ClimbingCenter c " +
            "where c.id = :centerId")
    Optional<String> findCenterImage(@Param("centerId") Long centerId);
}
