package climbing.climbBack.battleRoom.repository;

import climbing.climbBack.battleRoom.domain.CrewMan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CrewManRepository extends JpaRepository<CrewMan, Long> {

    // UserId 를 받아서 Mapping 되는 CrewId 를 return 하는 Query
    @Query("select cm.crew.id " +
            "from CrewMan cm " +
            "where cm.users.id = :userId")
    Long findCrewIdByUserId(@Param("userId") Long userId);
}
