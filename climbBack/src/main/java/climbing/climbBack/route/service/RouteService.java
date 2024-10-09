package climbing.climbBack.route.service;

import climbing.climbBack.route.domain.Difficulty;
import climbing.climbBack.route.domain.Route;
import climbing.climbBack.route.repository.RouteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RouteService {

    private final RouteRepository routeRepository;
    private final RouteGroupService routeGroupService;

    // 루트 등록 서비스
    @Transactional
    public void createRoute(Long routeId, Difficulty difficulty) {
        Route route = new Route();

        // routeId & difficulty 주입
        route.setId(routeId);
        route.setDifficulty(difficulty);

        routeRepository.save(route);
    }

    // 루트 삭제 서비스
    // 루트 삭제 -> 루트와 관련된 모든 간섭 관계 삭제
    @Transactional
    public void cancelRoute(Long routeId) {
        // routeId 를 가진 Data 삭제
        routeRepository.deleteById(routeId);

        // routeId 와 관련된 간섭 관계 Data 전부 삭제
        routeGroupService.deleteRelatedRouteGroup(routeId);
    }

    // routeId 를 가진 Data 가 존재 한다면 true, 그렇지 않다면 false return
    @Transactional(readOnly = true)
    public boolean checkRouteIn(Long routeId) {
        Optional<Route> route = routeRepository.findById(routeId);

        return route.isPresent();
    }
}
