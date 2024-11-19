package climbing.climbBack.battleRoom.repository;

import climbing.climbBack.battleRoom.domain.BattleSearchDto;
import climbing.climbBack.battleRoom.domain.Participant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ParticipantRepository extends JpaRepository<Participant, Long> {

    // battleRoom ID 와 Mapping 되는 모든 Data 삭제
    @Modifying
    @Query("delete from Participant p " +
            "where p.battleRoom.id = :battleRoomId")
    void deleteAllByBattleRoomId(@Param("battleRoomId") Long battleRoomId);

    // User 가 참여한 모든 BattleRoom 정보 조회 Query
    @Query("select distinct new climbing.climbBack.battleRoom.domain.BattleSearchDto(br.id, br.title, au.nickname, r.id, br.progress) " +
            "from Participant p " +
            "join p.battleRoom br join br.route r join br.adminUser au " +
            "where p.users.id = :userId")
    List<BattleSearchDto> findAllBattleByUser(@Param("userId") Long userId);
}
