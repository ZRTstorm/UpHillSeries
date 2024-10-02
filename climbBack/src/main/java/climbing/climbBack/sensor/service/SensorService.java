package climbing.climbBack.sensor.service;

import climbing.climbBack.sensor.domain.Sensor;
import climbing.climbBack.sensor.repository.SensorRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class SensorService {

    private final SensorRepository sensorRepository;

    // 센서 등록 성공 -> 등록한 센서의 고유 번호 반환 >0
    // 센서 등록 실패 -> -1 값 반환
    @Transactional
    public Long saveSensor(Sensor sensor) {
        if (validateDuplicateId(sensor)) {
            Sensor getSensor = sensorRepository.save(sensor);

            return getSensor.getId();
        } else {
            return -1L;
        }
    }

    // 받은 고유 번호를 가진 센서가 존재 하는지 확인
    // 센서 존재 -> true , 센서 존재 x -> false
    public boolean isSensor(Long sensorId) {
        Optional<Sensor> optionalSensor = sensorRepository.findById(sensorId);

        return optionalSensor.isPresent();
    }

    // 센서 삭제
    public boolean deleteSensor(Long sensorId) {
        try {
            sensorRepository.deleteById(sensorId);
            return true;
        } catch (EmptyResultDataAccessException e) {
            log.warn("error: ", e);
            return false;
        }
    }

    // sensorId 와 매칭 되는 센서가 존재 하지 않는 다면 true, 그렇지 않다면 false
    private boolean validateDuplicateId(Sensor sensor) {
        Optional<Sensor> preSensor = sensorRepository.findById(sensor.getId());

        return preSensor.isEmpty();
    }
}
