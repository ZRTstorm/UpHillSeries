package climbing.climbBack.route.service;

import climbing.climbBack.route.domain.RouteGroup;
import climbing.climbBack.route.repository.RouteGroupRepository;
import climbing.climbBack.route.repository.RouteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RouteGroupService {

    private final RouteGroupRepository routeGroupRepository;
    private final RouteRepository routeRepository;

    // 루트 간 간섭 관계 저장 서비스 -> route1 & route2
    @Transactional
    public void saveRouteGroup(Long route1, Long route2) {
        RouteGroup routeGroup = new RouteGroup();

        // route1 & route2 등록
        routeGroup.setRoute1(routeRepository.getReferenceById(route1));
        routeGroup.setRoute2(routeRepository.getReferenceById(route2));

        // routeGroup DB 저장
        routeGroupRepository.save(routeGroup);
    }

    // 루트 간 간섭 관계 저장 서비스 -> route & routeList
    @Transactional
    public void saveRouteGroupList(Long routeId, List<Long> routeList) {
        for (Long related : routeList) {
            RouteGroup routeGroup = new RouteGroup();

            // route 와 관련된 각 route2 등록
            routeGroup.setRoute1(routeRepository.getReferenceById(routeId));
            routeGroup.setRoute2(routeRepository.getReferenceById(related));

            // routeGroup DB 저장
            routeGroupRepository.save(routeGroup);
        }
    }

    // route Data 삭제에 따른 관련된 간섭 관계 전부 삭제 서비스
    @Transactional
    public void deleteRelatedRouteGroup(Long routeId) {
        routeGroupRepository.deleteAllByRouteId(routeId);
    }

    // parameter 의 routeId 와 간섭 관계를 가진 모든 routeId List 반환
    @Transactional(readOnly = true)
    public List<Long> getGroupById(Long routeId) {
        return routeGroupRepository.findAllRelatedRoute(routeId);
    }
}
