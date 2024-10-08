package climbing.climbBack.route.controller;

import climbing.climbBack.route.domain.RouteGroupDto;
import climbing.climbBack.route.service.RouteGroupService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class RouteController {

    private final RouteGroupService routeGroupService;

    // 루트 간 간섭 관계 등록 Controller
    @PostMapping("routes/routeGroup/register")
    public void registerRouteGroup(@RequestBody RouteGroupDto groupDto) {

        // route1 & route2 ID 추출
        Long route1 = groupDto.getRoute1();
        Long route2 = groupDto.getRoute2();

        // 루트 간 간섭 관계 DB 저장
        routeGroupService.saveRouteGroup(route1, route2);
    }
}
