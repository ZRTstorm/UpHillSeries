package climbing.climbBack.sensor.service;

import climbing.climbBack.sensor.domain.Sensor;
import climbing.climbBack.sensor.repository.SensorRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class SensorService {

    private final SensorRepository sensorRepository;

    // 센서 등록 서비스
    @Transactional
    public void saveSensor(Sensor sensor) {
        Long sensorId = sensor.getId();

        // 이미 등록된 센서 번호가 존재 하는지 검사
        if(sensorRepository.existsById(sensorId)) {
            log.info("SensorId is already exist = {}", sensorId);
            return;
        }

        sensorRepository.save(sensor);
    }

    // sensorId 를 가진 센서가 등록 되어 있는지 검사
    // return : 존재 하면 true , 존재 하지 않다면 false
    public boolean isSensor(Long sensorId) {
        return sensorRepository.existsById(sensorId);
    }

    // sensorId 를 가진 센서가 부착된 routeId 확인
    public Long getRouteBySensor(Long sensorId) {
        Optional<Sensor> sensor = sensorRepository.findById(sensorId);
        if (sensor.isEmpty()) {
            throw new IllegalStateException("Please check for isSensor method");
        }

        return sensor.get().getRouteId();
    }

    // route 에 부착된 센서 List 파악 서비스
    public List<Sensor> getSensorListByRoute(Long routeId) {
        return sensorRepository.findByRouteId(routeId);
    }

    // 센서 삭제 서비스
    public void deleteSensor(Long sensorId) {
        if (!sensorRepository.existsById(sensorId)) {
            log.info("SensorId is not used yet = {}", sensorId);
            return;
        }

        sensorRepository.deleteById(sensorId);
    }
}
