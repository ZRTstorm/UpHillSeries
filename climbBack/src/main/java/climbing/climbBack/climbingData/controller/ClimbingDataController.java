package climbing.climbBack.climbingData.controller;

import climbing.climbBack.climbingData.domain.ClimbingDataDto;
import climbing.climbBack.climbingData.service.ClimbingDataService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/climbing")
@RequiredArgsConstructor
@Slf4j
public class ClimbingDataController {

    private final ClimbingDataService climbingDataService;

    // 등반 시작 감지 Controller
    @PostMapping("/{sensorId}/start")
    @Operation(summary = "등반 기록 생성", description = "등반 시작을 인지한 센서의 요청을 받아 등반 기록을 생성한다")
    public ResponseEntity<Void> startClimbing(
            @Parameter(description = "시작 홀드의 센서 ID") @PathVariable Long sensorId) {

        // Route 를 등반 중인 User 가 존재 하는지 확인
        // checkUserInRoute : 등반 User 가 존재하면 false , 없다면 true return
        if (climbingDataService.checkUserInRoute(sensorId)) {
            log.info("Start climbing - User is not in Route Problem : sensor = {}", sensorId);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        // 등반 기록 시작
        // DB 관련 Client 측 요청 문제는 BAD Request 처리
        try {
            climbingDataService.createClimbingData(sensorId);
        } catch (RuntimeException e) {
            log.info("Start climbing - {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        return ResponseEntity.status(HttpStatus.OK).build();
    }

    // 등반 성공 감지 Controller
    @PatchMapping("/{sensorId}/success")
    @Operation(summary = "등반 성공 기록 완성", description = "등반 성공 요청을 받아 경로 상의 기록을 성공 처리한다")
    public ResponseEntity<Void> successClimbing(
            @Parameter(description = "탑 홀드의 센서 ID") @PathVariable Long sensorId) {

        // Route 를 등반 중인 User 가 존재 하는지 확인
        if (climbingDataService.checkUserInRoute(sensorId)) {
            log.info("Success Climbing - User is not in Route Problem : sensor = {}", sensorId);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        // 등반 기록 성공 처리
        // DB 관련 Client 측 요청 문제는 BAD Request 처리
        try {
            climbingDataService.successClimbingData(sensorId);
        } catch (RuntimeException e) {
            log.info("Success climbing - {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        return ResponseEntity.status(HttpStatus.OK).build();
    }

    // 등반 실패 감지 Controller
    @PatchMapping("/{userId}/failure")
    @Operation(summary = "등반 실패 기록 완성", description = "Client 로부터 등반 실패 요청을 받아 경로 상의 기록을 실패 처리한다")
    public ResponseEntity<Map<String, String>> failureClimbing(
            @Parameter(description = "요청을 보내는 App User 의 ID") @PathVariable Long userId) {

        // 응답 용 객체
        Map<String, String> response = new HashMap<>();

        // 센서 로부터 받지 않고, App Client 로부터 도달
        try {
            climbingDataService.failureClimbingData(userId);
        } catch (Exception e) {
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        response.put("message", "Climbing data is completed for failure");
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    // 등반 기록 삭제 Controller
    @DeleteMapping("/{climbingDataId}/data")
    @Operation(summary = "등반 기록 삭제", description = "등반 기록 ID 와 일치 하는 기록을 삭제한다")
    public ResponseEntity<Void> deleteClimbing(
            @Parameter(description = "삭제할 등반 기록의 ID") @PathVariable Long climbingDataId) {

        // 등반 기록 삭제 처리
        // climbingDataId 와 Matching 되는 데이터가 없는 경우 Bad Request 처리
        try {
            climbingDataService.deleteClimbingData(climbingDataId);
        } catch (DataAccessException e) {
            log.info("Delete climbingData - {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        return ResponseEntity.status(HttpStatus.OK).build();
    }

    // 등반 기록 전체 조회 Controller
    @GetMapping("/data")
    @Operation(summary = "등반 기록 전체 조회", description = "DB 에 저장된 모든 등반 기록을 조회한다")
    public List<ClimbingDataDto> getAllClimbing() {

        return climbingDataService.getAllClimbingData();
    }

    // 등반 기록 사용자 별 조회 Controller
    @GetMapping("/users/{userId}/data")
    @Operation(summary = "사용자 등반 기록 전체 조회", description = "DB 에 저장된 사용자 별 모든 등반 기록을 조회한다")
    public List<ClimbingDataDto> getAllUserClimbing(
            @Parameter(description = "요청을 보내는 App User 의 ID") @PathVariable Long userId) {

        return climbingDataService.getAllUserClimbingData(userId);
    }
}
