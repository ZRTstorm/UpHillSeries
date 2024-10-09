package climbing.climbBack.route.repository;

import climbing.climbBack.route.domain.Route;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RouteRepository extends JpaRepository<Route, Long> {

}
