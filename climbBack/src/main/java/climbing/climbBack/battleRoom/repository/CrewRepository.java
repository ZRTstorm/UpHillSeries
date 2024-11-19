package climbing.climbBack.battleRoom.repository;

import climbing.climbBack.battleRoom.domain.Crew;
import climbing.climbBack.battleRoom.domain.CrewManSearchDto;
import climbing.climbBack.battleRoom.domain.CrewSearchDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CrewRepository extends JpaRepository<Crew, Long> {

    // Crew 전체 조회 Query
    @Query("select new climbing.climbBack.battleRoom.domain.CrewSearchDto(c.id, c.crewName, c.content, au.nickname) " +
            "from Crew c join fetch c.adminUser au")
    List<CrewSearchDto> findAllCrew();

    // Crew 이름 Crew 조회 Query
    @Query("select new climbing.climbBack.battleRoom.domain.CrewSearchDto(c.id, c.crewName, c.content, au.nickname) " +
            "from Crew c join fetch c.adminUser au " +
            "where c.crewName = :crewName")
    List<CrewSearchDto> findCrewByName(@Param("crewName") String crewName);

    // Crew Image 조회 Query
    @Query("select c.crewIcon from Crew c where c.id = :crewId")
    Optional<String> findCrewImage(@Param("crewId") Long crewId);

}
