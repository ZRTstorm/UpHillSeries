package climbing.climbBack.route.repository;

import climbing.climbBack.route.domain.RouteGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RouteGroupRepository  extends JpaRepository<RouteGroup, Long> {

    // routeId 와 연관된 간섭 관계 기록을 전부 삭제
    // routeId 가 route1 or route2 에 존재 하면 삭제
    @Modifying
    @Query("delete from RouteGroup r where r.route1 = :routeId or r.route2 = :routeId")
    void deleteAllByRouteId(@Param("routeId") Long routeId);

    // routeId 와 연관된 모든 간섭 관계를 찾아 반환
    // RouteGroup 에서 route1 or route2 에 routeId 값을 가지고 있는 Data 중에
    // route1 이 routeId 라면 route2 를, route2 라면 route1 을 List 로 모아서 반환
    @Query("select case when r.route1 = :routeId then r.route2 else r.route1 end " +
            "from RouteGroup r " +
            "where r.route1 = :routeId or r.route2 = :routeId")
    List<Long> findAllRelatedRoute(@Param("routeId") Long routeId);
}
