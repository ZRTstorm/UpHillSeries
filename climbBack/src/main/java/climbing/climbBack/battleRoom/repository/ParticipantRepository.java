package climbing.climbBack.battleRoom.repository;

import climbing.climbBack.battleRoom.domain.BattleDataDto;
import climbing.climbBack.battleRoom.domain.BattleSearchDto;
import climbing.climbBack.battleRoom.domain.Participant;
import climbing.climbBack.battleRoom.domain.ParticipantDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ParticipantRepository extends JpaRepository<Participant, Long> {

    // battleRoom ID 와 Mapping 되는 모든 Data 삭제
    @Modifying
    @Query("delete from Participant p " +
            "where p.battleRoom.id = :battleRoomId")
    void deleteAllByBattleRoomId(@Param("battleRoomId") Long battleRoomId);

    // User 가 참여한 모든 BattleRoom 정보 조회 Query
    @Query("select new climbing.climbBack.battleRoom.domain.BattleSearchDto(" +
            "p.battleRoom.id, p.battleRoom.title, p.battleRoom.content, p.battleRoom.adminUser.nickname, p.battleRoom.route.id, p.battleRoom.progress) " +
            "from Participant p " +
            "where p.users.id = :userId")
    List<BattleSearchDto> findAllBattleByUser(@Param("userId") Long userId);

    // BattleRoomId & UserId 와 Matching 되는 Participant 조회 Query
    @Query("select p from Participant p where p.battleRoom.id = :battleRoomId and p.users.id = :userId")
    Optional<Participant> findParticipantByBattleAndUser(@Param("battleRoomId") Long battleRoomId,
                                                         @Param("userId") Long userId);

    // battleRoom 에 등록된 모든 ClimbingData 조회 Query
    @Query("select new climbing.climbBack.battleRoom.domain.BattleDataDto(" +
            "cd.id, u.nickname, cd.success, cd.climbingTime) " +
            "from Participant p " +
            "join p.climbingData cd join cd.users u " +
            "where p.battleRoom.id = :battleRoomId")
    List<BattleDataDto> findBattleDtoById(@Param("battleRoomId") Long battleRoomId);

    // BattleRoom 에 참여한 모든 Participant 조회
    @Query("select new climbing.climbBack.battleRoom.domain.ParticipantDto(p.id, p.users.nickname) " +
            "from Participant p " +
            "where p.battleRoom.id = :battleRoomId")
    List<ParticipantDto> findParticipantByBattle(@Param("battleRoomId") Long battleRoomId);
}
