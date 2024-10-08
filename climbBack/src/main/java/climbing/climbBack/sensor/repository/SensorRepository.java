package climbing.climbBack.sensor.repository;

import climbing.climbBack.sensor.domain.Sensor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SensorRepository extends JpaRepository<Sensor, Long> {

    List<Sensor> findByRouteId(Long routeId);
}
