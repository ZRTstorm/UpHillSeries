package climbing.climbBack.route.repository;

import climbing.climbBack.route.domain.Route;
import climbing.climbBack.route.domain.RouteGetDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RouteRepository extends JpaRepository<Route, Long> {

    // 전체 Route List 조회 Query ( 이미지 제외 )
    @Query("select new climbing.climbBack.route.domain.RouteGetDto(r.id, r.difficulty, c.id, r.xPos, r.yPos) " +
            "from Route r join r.climbingCenter c")
    List<RouteGetDto> findAllRoutesExceptImage();

    // centerId 와 매칭 되는 Route List 조회 Query ( 이미지 제외 )
    @Query("select new climbing.climbBack.route.domain.RouteGetDto(r.id, r.difficulty, c.id, r.xPos, r.yPos) " +
            "from Route r join r.climbingCenter c " +
            "where c.id = :centerId")
    List<RouteGetDto> findRoutesByCenterIdExceptImage(@Param("centerId") Long centerId);
}
