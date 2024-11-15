package climbing.climbBack.route.controller;

import climbing.climbBack.route.domain.*;
import climbing.climbBack.route.repository.ClimbingCenterRepository;
import climbing.climbBack.route.service.RouteGroupService;
import climbing.climbBack.route.service.RouteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/routes")
@RequiredArgsConstructor
@Slf4j
public class RouteController {

    private final RouteGroupService routeGroupService;
    private final RouteService routeService;

    // 루트 간 간섭 관계 등록 Controller
    @PostMapping("/routeGroup/register")
    @Operation(summary = "루트 간섭 관계 등록", description = "간섭 관계를 가진 두 루트의 ID 를 연관 짓는다")
    public void registerRouteGroup(@RequestBody RouteGroupDto groupDto) {

        // route1 & route2 ID 추출
        Long route1 = groupDto.getRoute1();
        Long route2 = groupDto.getRoute2();

        // 루트 간 간섭 관계 DB 저장
        routeGroupService.saveRouteGroup(route1, route2);
    }

    // 특정 루트의 간섭 관계 루트 List 조회 Controller
    @GetMapping("/routeGroup/{routeId}")
    @Operation(summary = "루트 별 간섭 관계 조회", description = "특정 루트의 간섭 관계 루트 List 를 조회한다")
    public List<Long> getInferListByRoute(
            @Parameter(description = "조회 하고자 하는 루트의 ID") @PathVariable Long routeId) {

        return routeGroupService.getGroupById(routeId);
    }

    // 루트 등록 Controller
    @PostMapping("/route/register")
    @Operation(summary = "루트 등록", description = "이미지 데이터를 제외한 모든 정보를 가진 루트 데이터를 저장한다")
    public ResponseEntity<Void> registerRoute(@RequestBody RouteDto routeDto) {

        // Route ID 추출
        Long routeId = routeDto.getRouteId();

        // 등록 하고자 하는 routeId 가 이미 존재 하는지 검사
        if (routeService.checkRouteIn(routeId)) {
            log.info("This routeId is already registered = {}", routeId);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        try {
            routeService.createRoute(routeDto);
        } catch (RuntimeException e) {
            log.info("RegisterRouteError = {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        return ResponseEntity.status(HttpStatus.OK).build();
    }

    // 암장 등록 Controller
    @PostMapping("/climbingCenter/register")
    @Operation(summary = "암장 등록", description = "이미지 데이터를 제외한 암장 정보를 저장한다")
    public ResponseEntity<?> registerCenter(@RequestBody String centerName) {

        routeService.createCenter(centerName);

        return ResponseEntity.status(HttpStatus.OK).body("Center is Saved");
    }

    // 모든 루트 조회 Controller
    @GetMapping("/route")
    @Operation(summary = "모든 루트 조회", description = "이미지 데이터를 제외한 루트의 모든 정보를 조회한다")
    public List<RouteGetDto> getAllRoutesExceptImage() {

        return routeService.findAllRoutes();
    }

    // 암장 별 모든 루트 조회 Controller
    @GetMapping("/route/{climbingCenterId}")
    @Operation(summary = "암장 별 루트 조회", description = "이미지 데이터를 제외한 암장 별 루트의 모든 정보를 조회한다")
    public List<RouteGetDto> getRouteByCenterExceptImage(
            @Parameter(description = "조회할 암장의 ID") @PathVariable Long climbingCenterId) {

        return routeService.findRoutesByCenter(climbingCenterId);
    }

    // 모든 암장 조회 Controller
    @GetMapping("/climbingCenter")
    @Operation(summary = "모든 암장 조회", description = "이미지 데이터를 제외한 모든 암장 정보를 조회한다")
    public List<CenterGetDto> getAllCentersExceptImage() {

        return routeService.findAllCenters();
    }

    // 루트 삭제 Controller
    @DeleteMapping("/route/{routeId}")
    @Operation(summary = "루트 삭제", description = "DB 에 저장된 루트를 삭제한다")
    public void deleteRoute(@PathVariable Long routeId) {

        // 삭제 하고자 하는 routeId 가 존재 하는지 검사
        if (!routeService.checkRouteIn(routeId)) {
            log.info("This routeId is not in DB = {}", routeId);
            return;
        }

        routeService.cancelRoute(routeId);
    }

    // 루트 이미지 저장 Controller
    @PostMapping("/{routeId}/upload")
    @Operation(summary = "루트 이미지 저장", description = "DB 에 저장된 루트에 대해 이미지 데이터를 추가한다")
    public ResponseEntity<Map<String, String>> uploadRouteImage(
            @Parameter(description = "이미지를 저장하는 루트 ID") @PathVariable Long routeId,
            @RequestBody String imageData) {

        // 응답용 객체 생성
        Map<String, String> response = new HashMap<>();

        // 루트 이미지 저장
        try {
            routeService.saveRouteImage(routeId, imageData);

            response.put("message", "Image saved Successfully");
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (IllegalStateException e) {
            log.info("UploadImageError = {}", e.getMessage());

            response.put("errorMessage", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        } catch (Exception e) {
            log.info("UploadImageError = {}", e.getMessage());

            response.put("errorMessage", e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    // 루트 이미지 조회 Controller
    @GetMapping("/{routeId}/routeImage")
    @Operation(summary = "루트 이미지 조회", description = "루트 이미지 및 좌표를 조회 한다")
    public RouteImageDto getRouteImage(
            @Parameter(description = "조회할 루트 ID") @PathVariable Long routeId) {

        return routeService.getRouteImage(routeId);
    }

    // 암장 이미지 조회 Controller
    @GetMapping("/{climbingCenterId}/centerImage")
    @Operation(summary = "암장 이미지 조회", description = "암장 ID 와 일치하는 이미지 데이터를 조회한다")
    public CenterImageDto getCenterImage(
            @Parameter(description = "조회할 암장 ID") @PathVariable Long climbingCenterId) {

        try {
            return routeService.getCenterImage(climbingCenterId);
        } catch (RuntimeException e) {
            return new CenterImageDto();
        }
    }
}
