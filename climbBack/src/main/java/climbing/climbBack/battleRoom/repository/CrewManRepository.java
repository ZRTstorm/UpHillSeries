package climbing.climbBack.battleRoom.repository;

import climbing.climbBack.battleRoom.domain.CrewMan;
import climbing.climbBack.battleRoom.domain.CrewManSearchDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CrewManRepository extends JpaRepository<CrewMan, Long> {

    // UserId 를 받아서 Mapping 되는 CrewId 를 return 하는 Query
    @Query("select cm.crew.id " +
            "from CrewMan cm " +
            "where cm.users.id = :userId")
    Long findCrewIdByUserId(@Param("userId") Long userId);

    // User 가 Crew 에 속해 있는지 확인 하는 Query
    @Query("select case when count(cm) > 0 then true else false end " +
            "from CrewMan cm where cm.users.id = :userId")
    boolean existsByUserId(@Param("userId") Long userId);

    // 크루에 속한 크루원 전체 삭제 Query
    @Modifying
    @Query("delete from CrewMan cm where cm.crew.id = :crewId")
    void deleteAllFromCrew(@Param("crewId") Long crewId);

    // User 의 크루원 삭제 Query
    @Modifying
    @Query("delete from CrewMan cm where cm.users.id = :userId")
    void deleteByUserId(@Param("userId") Long userId);

    // 크루원 전체 조회 Query
    @Query("select new climbing.climbBack.battleRoom.domain.CrewManSearchDto(cm.id, u.nickname) " +
            "from CrewMan cm join fetch Crew c join fetch Users u " +
            "where c.id = :crewId")
    List<CrewManSearchDto> findAllCrewMans(@Param("crewId") Long crewId);
}
