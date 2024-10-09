package climbing.climbBack.sensor.controller;

import climbing.climbBack.sensor.domain.SensorRegisterDto;
import climbing.climbBack.sensor.service.SensorService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class SensorController {

    private final SensorService sensorService;

    // 센서 등록 Controller
    @PostMapping("/sensors/register")
    public void registerSensor(@Valid @RequestBody SensorRegisterDto sensorRegisterDto) {

        // sensorId & routeId 추출
        Long sensorId = sensorRegisterDto.getSensorId();
        Long routeId = sensorRegisterDto.getRouteId();

        // 센서 등록
        sensorService.saveSensor(sensorId, routeId);
    }

    // 센서 삭제 Controller
    @PostMapping("/sensors/{sensorId}/delete")
    public void removeSensor(@PathVariable @Min(1) Long sensorId) {

        // 센서 삭제
        sensorService.deleteSensor(sensorId);
    }

    // parameter : sensorId
    // 센서 List Controller
}
