package climbing.climbBack.sensorData.controller;

import climbing.climbBack.sensorData.domain.SensorData;
import climbing.climbBack.sensorData.domain.SensorDataDto;
import climbing.climbBack.sensorData.service.SensorDataService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequiredArgsConstructor
public class SensorDataController {

    private final SensorDataService sensorDataService;

    // QueueEntryService Layer 에서 현재 루트를 사용 하고 있는 유저가 있는지 확인
    // 센서 Data 는 사용 하는 유저가 없는 경우 의미 없는 데이터 -> 요청 와도 버려
    // method 화 -> 모든 controller 에서 사용 해야 하는 매서드

    @PostMapping("/sensorData/{sensorId}/start")
    public boolean startSensing(@Valid @RequestBody SensorDataDto sensorDataDto, @PathVariable Long sensorId) {
        // 들어온 Data 의 유저가 없는 경우 -> false return
        if (!isDataUser(sensorDataDto.getRouteId())) return false;
        // SensorDataDto -> sensorData
        SensorData sensorData = getDataToDto(sensorDataDto, sensorId);

        return sensorDataService.setSensorList(sensorData);
    }

    @PostMapping("/sensorData/{sensorId}/insert")
    public boolean pushSensing(@Valid @RequestBody SensorDataDto sensorDataDto, @PathVariable Long sensorId) {
        if (!isDataUser(sensorDataDto.getRouteId())) return false;

        SensorData sensorData = getDataToDto(sensorDataDto, sensorId);

        sensorDataService.addSensorData(sensorData);
        return true;
    }

    @PostMapping("/sensorData/{sensorId}/end")
    public boolean endSensing(@Valid @RequestBody SensorDataDto sensorDataDto, @PathVariable Long sensorId) {
        if (!isDataUser(sensorDataDto.getRouteId())) return false;

        SensorData sensorData = getDataToDto(sensorDataDto, sensorId);

        return sensorDataService.endSensorList(sensorData);
    }

    // QueueEntryService -> route : User Data 가 존재 하면 true , 아니면 false
    private boolean isDataUser(Long routeId) {

        return true;
    }

    private SensorData getDataToDto(SensorDataDto sensorDataDto, Long sensorId) {

        SensorData sensorData = new SensorData();

        // Sensor 프록시 객체 생성 -> sensorId 만 보유 -> Service Layer 로 내려
        sensorData.setRouteId(sensorDataDto.getRouteId());
        sensorData.setTouched(sensorDataDto.isTouched());
        sensorData.setCreatedTime(LocalDateTime.now());

        return sensorData;
    }
}
