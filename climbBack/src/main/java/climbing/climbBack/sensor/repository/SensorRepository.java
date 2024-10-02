package climbing.climbBack.sensor.repository;

import climbing.climbBack.sensor.domain.Sensor;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SensorRepository extends JpaRepository<Sensor, Long> {

}
