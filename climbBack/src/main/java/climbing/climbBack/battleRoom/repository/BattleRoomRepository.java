package climbing.climbBack.battleRoom.repository;

import climbing.climbBack.battleRoom.domain.BattleRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface BattleRoomRepository extends JpaRepository<BattleRoom, Long> {

    // 참여 코드 사용 BattleRoom 조회 Query
    @Query("select br " +
            "from BattleRoom br " +
            "where br.participantCode = :participantCode")
    Optional<BattleRoom> searchRoomForCode(@Param("participantCode") String participantCode);

    // crewId 와 일치 하는 모든 BattleRoom 조회 Query
    @Query("select distinct br " +
            "from BattleRoom br join fetch br.route r join fetch br.adminUser au " +
            "where br.crewId = :crewId")
    List<BattleRoom> findAllRoomByCrew(@Param("crewId") Long crewId);

    // BattleRoom 기본 조회 Query
    @Query("select br " +
            "from BattleRoom br join fetch br.route r join fetch br.adminUser au " +
            "where br.id = :battleRoomId")
    BattleRoom findBattleRoomInfo(@Param("battleRoomId") Long battleRoomId);
}
