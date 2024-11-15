package climbing.climbBack.route.service;

import climbing.climbBack.route.domain.*;
import climbing.climbBack.route.repository.ClimbingCenterRepository;
import climbing.climbBack.route.repository.RouteRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class RouteService {

    private final RouteRepository routeRepository;
    private final RouteGroupService routeGroupService;
    private final ClimbingCenterRepository climbingCenterRepository;

    // 루트 등록 서비스
    @Transactional
    public void createRoute(RouteDto routeDto) {
        Route route = new Route();

        // Route Info 주입
        route.setId(routeDto.getRouteId());
        route.setDifficulty(routeDto.getDifficulty());
        route.setClimbingCenter(climbingCenterRepository.getReferenceById(routeDto.getClimbingCenterId()));
        route.setStartX(routeDto.getStartX());
        route.setStartY(routeDto.getStartY());
        route.setEndX(routeDto.getEndX());
        route.setEndY(routeDto.getEndY());

        routeRepository.save(route);
    }

    // 암장 등록 서비스
    @Transactional
    public void createCenter(String centerName) {
        ClimbingCenter climbingCenter = new ClimbingCenter();
        climbingCenter.setCenterName(centerName);

        climbingCenterRepository.save(climbingCenter);
    }

    // 루트 조회 서비스 ( Image Data 제외 )
    // { routeId , difficulty , centerId }
    @Transactional(readOnly = true)
    public List<RouteGetDto> findAllRoutes() {
        return routeRepository.findAllRoutesExceptImage();
    }

    // 암장 별 루트 조회 서비스 ( Image Data 제외 )
    // { routeId , difficulty , centerId }
    @Transactional(readOnly = true)
    public List<RouteGetDto> findRoutesByCenter(Long centerId) {
        return routeRepository.findRoutesByCenterIdExceptImage(centerId);
    }

    // 암장 전체 조회 서비스
    @Transactional(readOnly = true)
    public List<CenterGetDto> findAllCenters() {
        return climbingCenterRepository.findAllCenters();
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

    // 루트 이미지 저장 서비스
    @Transactional
    public void saveRouteImage(Long routeId, String imageData) {
        // 루트 Data 획득
        Route route = routeRepository.findById(routeId)
                .orElseThrow(() -> new IllegalStateException("Route is not in DB : route = " + routeId));

        route.setImageData(imageData);
    }

    // 루트 이미지 조회 서비스
    @Transactional(readOnly = true)
    public RouteImageDto getRouteImage(Long routeId) {
        // 루트 Data 획득
        Route route = routeRepository.findById(routeId)
                .orElseThrow(() -> new IllegalStateException("Route is not in DB : route = " + routeId));

        // RouteImageDto 채우기
        RouteImageDto dto = new RouteImageDto();
        dto.setImageData(route.getImageData());
        dto.setStartX(route.getStartX());
        dto.setStartY(route.getStartY());
        dto.setEndX(route.getEndX());
        dto.setEndY(route.getEndY());

        return dto;
    }

    // 암장 이미지 조회 서비스
    @Transactional(readOnly = true)
    public CenterImageDto getCenterImage(Long centerId) {
        Optional<String> centerImage = climbingCenterRepository.findCenterImage(centerId);

        if (centerImage.isEmpty()) {
            log.info("CenterImage is not in DB : centerId = {}", centerId);
            throw new IllegalStateException("CenterImage is not in DB");
        }

        return new CenterImageDto(centerImage.get());
    }

    // 루트 이미지 저장 서비스 Previous
    // Binary Image Data -> Base64 Encoding String Data
    @Transactional
    public void saveRouteImagePrevious(Long routeId, MultipartFile multipartFile) throws IOException {
        // 루트 Data 획득
        Route route = routeRepository.findById(routeId)
                .orElseThrow(() -> new IllegalStateException("Route is not in DB : route = " + routeId));

        // 이미지 파일을 Base64 로 인코딩
        String encodedImage = Base64.getEncoder().encodeToString(multipartFile.getBytes());

        // 인코딩 이미지 데이터를 루트에 저장
        route.setImageData(encodedImage);
    }
}
