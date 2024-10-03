package climbing.climbBack.sensorData.repository;

import climbing.climbBack.sensorData.domain.SensorData;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SensorDataJpaRepository extends JpaRepository<SensorData, Long> {

}
