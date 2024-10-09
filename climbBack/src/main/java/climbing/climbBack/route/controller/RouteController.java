package climbing.climbBack.route.controller;

import climbing.climbBack.route.domain.Difficulty;
import climbing.climbBack.route.domain.RouteDto;
import climbing.climbBack.route.domain.RouteGroupDto;
import climbing.climbBack.route.service.RouteGroupService;
import climbing.climbBack.route.service.RouteService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class RouteController {

    private final RouteGroupService routeGroupService;
    private final RouteService routeService;

    // 루트 간 간섭 관계 등록 Controller
    @PostMapping("routes/routeGroup/register")
    public void registerRouteGroup(@RequestBody RouteGroupDto groupDto) {

        // route1 & route2 ID 추출
        Long route1 = groupDto.getRoute1();
        Long route2 = groupDto.getRoute2();

        // 루트 간 간섭 관계 DB 저장
        routeGroupService.saveRouteGroup(route1, route2);
    }

    // 루트 등록 Controller
    @PostMapping("routes/route/register")
    public void registerRoute(@RequestBody RouteDto routeDto) {

        Long routeId = routeDto.getRouteId();
        Difficulty difficulty = routeDto.getDifficulty();

        // 등록 하고자 하는 routeId 가 이미 존재 하는지 검사
        if (routeService.checkRouteIn(routeId)) {
            log.info("This routeId is already registered = {}", routeId);
            return;
        }

        routeService.createRoute(routeId, difficulty);
    }

    // 루트 삭제 Controller
    @PostMapping("routes/route/{routeId}/delete")
    public void deleteRoute(@PathVariable Long routeId) {

        // 삭제 하고자 하는 routeId 가 존재 하는지 검사
        if (!routeService.checkRouteIn(routeId)) {
            log.info("This routeId is not in DB = {}", routeId);
            return;
        }

        routeService.cancelRoute(routeId);
    }
}
