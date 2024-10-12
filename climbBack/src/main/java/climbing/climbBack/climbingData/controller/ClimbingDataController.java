package climbing.climbBack.climbingData.controller;

import climbing.climbBack.climbingData.domain.SensorDataDto;
import climbing.climbBack.climbingData.service.ClimbingDataService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@Slf4j
public class ClimbingDataController {

    private final ClimbingDataService climbingDataService;

    // 등반 시작 감지 Controller
    @PostMapping("/climbing/start")
    @Operation(summary = "등반 기록 생성", description = "등반 시작을 인지 하고 등반 기록을 생성 한다")
    @ApiResponse(responseCode = "200", description = "Success")
    @ApiResponse(responseCode = "400", description = "Bad Request")
    public ResponseEntity<Map<String, String>> startClimbing(
            @Parameter(description = "sensorId : Long (min 1) , isTouched : boolean")
            @RequestBody SensorDataDto dataDto) {

        // 응답 용 객체
        Map<String, String> response = new HashMap<>();

        // 센서 ID & 행동 상태 추출
        Long sensorId = dataDto.getSensorId();
        boolean touched = dataDto.isTouched();

        // 등반 시작 시점은 시작 홀드 에서 손을 뗀 시점 부터 -> false Data 만 취급
        if (touched) {
            log.info("Climbing Start Controller : Start Hold Data is needed for false Data");
            response.put("message", "Start Hold is needed for false Data");

            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST).body(response);
        }

        // route 를 등반 중인 user 가 있는지 확인
        if(climbingDataService.checkUserInRoute(sensorId)) {
            log.info("There are no Users in climbing yet with = {}", sensorId);
            response.put("message", "There are no Users in climbing yet with " + sensorId);
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST).body(response);
        }

        // 등반 기록 시작
        climbingDataService.createClimbingData(sensorId);

        response.put("message", "Climbing data is created");
        return ResponseEntity
                .status(HttpStatus.OK).body(response);
    }

    // 등반 성공 감지 Controller
    @PostMapping("climbing/success")
    @Operation(summary = "등반 성공 기록 완성", description = "센서 로부터 등반 성공을 확인 하고, 기록을 완성 한다")
    public ResponseEntity<Map<String, String>> successClimbing(@RequestBody SensorDataDto dataDto) {

        // 응답 용 객체
        Map<String, String> response = new HashMap<>();

        Long sensorId = dataDto.getSensorId();
        boolean touched = dataDto.isTouched();

        // 등반 성공 시점은 탑 홀드를 잡은 시점을 기준 -> true Data 만 취급
        if (!touched) {
            log.info("Top Hold Data is needed for true Data");
            response.put("message", "Top Hold is needed for true Data");

            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST).body(response);
        }

        // route 를 등반 중인 user 가 있는지 확인
        if(climbingDataService.checkUserInRoute(sensorId)) {
            log.info("User is not climbing in this route = {}", sensorId);
            response.put("message", "There are no Users in climbing yet with " + sensorId);
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST).body(response);
        }

        // 등반 성공 기록
        climbingDataService.successClimbingData(sensorId);

        response.put("message", "Climbing data is completed for success");
        return ResponseEntity
                .status(HttpStatus.OK).body(response);
    }

    // 등반 실패 감지 Controller
    @PostMapping("climbing/failure")
    @Operation(summary = "등반 실패 기록 완성", description = "Client 로부터 등반 실패를 확인 하고, 기록을 완성 한다")
    public ResponseEntity<Map<String, String>> failureClimbing(@RequestBody Long userId) {

        // 응답 용 객체
        Map<String, String> response = new HashMap<>();

        // 센서 로부터 받지 않고, App Client 로부터 도달
        try {
            climbingDataService.failureClimbingData(userId);
        } catch (Exception e) {
            response.put("message", e.getMessage());
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST).body(response);
        }

        response.put("message", "Climbing data is completed for failure");
        return ResponseEntity
                .status(HttpStatus.OK).body(response);
    }
}
