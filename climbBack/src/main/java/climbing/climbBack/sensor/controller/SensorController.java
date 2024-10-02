package climbing.climbBack.sensor.controller;

import climbing.climbBack.sensor.domain.Sensor;
import climbing.climbBack.sensor.domain.SensorDto;
import climbing.climbBack.sensor.service.SensorService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class SensorController {

    private final SensorService sensorService;

    // 센서 등록
    @PostMapping("/sensors/register")
    public Long registerSensor(@RequestBody SensorDto sensorDto) {

        Sensor sensor = new Sensor();
        sensor.setId(sensorDto.getId());
        sensor.setCenter(sensor.getCenter());
        sensor.setRoute(sensor.getRoute());

        return sensorService.saveSensor(sensor);
    }

    // 센서가 등록 되어 있는지 확인
    @GetMapping("/sensors/{sensorId}")
    public boolean checkSensor(@PathVariable Long sensorId) {

        return sensorService.isSensor(sensorId);
    }

    // 센서 리스트 검색 -> 암장 / 루트 별로 등록된 센서 리스트 반환

    // 센서 삭제
    @PostMapping("/sensors/{sensorId}/delete")
    public boolean removeSensor(@PathVariable Long sensorId) {

        return sensorService.deleteSensor(sensorId);
    }
}
