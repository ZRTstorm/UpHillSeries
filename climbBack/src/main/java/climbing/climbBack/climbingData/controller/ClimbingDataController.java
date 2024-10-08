package climbing.climbBack.climbingData.controller;

import climbing.climbBack.climbingData.domain.SensorDataDto;
import climbing.climbBack.climbingData.service.ClimbingDataService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class ClimbingDataController {

    private final ClimbingDataService climbingDataService;

    // 등반 시작 감지 Controller
    @PostMapping("/climbing/start")
    public void startClimbing(@RequestBody SensorDataDto dataDto) {

        Long sensorId = dataDto.getSensorId();
        boolean touched = dataDto.isTouched();

        // 등반 시작 시점은 시작 홀드 에서 손을 뗀 시점 부터 -> false Data 만 취급
        if (touched) {
            log.info("Start Hold Data is needed for false Data");
            return;
        }

        // route 를 등반 중인 user 가 있는지 확인
        if(!climbingDataService.checkUserInRoute(sensorId)) {
            log.info("User is not climbing in this route = {}", sensorId);
        }

        // 등반 기록 시작
        climbingDataService.createClimbingData(sensorId);
    }

    // 등반 성공 감지 Controller
    @PostMapping("climbing/success")
    public void successClimbing(@RequestBody SensorDataDto dataDto) {

        Long sensorId = dataDto.getSensorId();
        boolean touched = dataDto.isTouched();

        // 등반 성공 시점은 탑 홀드를 잡은 시점을 기준 -> true Data 만 취급
        if (!touched) {
            log.info("Top Hold Data is needed for true Data");
        }

        // route 를 등반 중인 user 가 있는지 확인
        if(!climbingDataService.checkUserInRoute(sensorId)) {
            log.info("User is not climbing in this route = {}", sensorId);
        }

        // 등반 성공 기록
        climbingDataService.successClimbingData(sensorId);
    }
}
