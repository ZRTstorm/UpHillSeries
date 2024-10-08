package climbing.climbBack.entryQueue.repository;

import climbing.climbBack.entryQueue.domain.EntryQueue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


import java.util.Optional;

public interface EntryQueueRepository extends JpaRepository<EntryQueue, Long> {

    // ByRouteId : parameter routeId 를 가진 Data 중에
    // OrderByPositionDesc : position 값을 기준 으로 내림 차순 정렬 하여
    // findFirst : 가장 첫 엔티티 반환
    // Optional 로 감싸서 반환 -> 값의 존재 여부 확인
    Optional<EntryQueue> findFirstByRouteIdOrderByPositionDesc(Long routeId);

    // 동일 routeId 값을 가진 Data 의 pos 값을 1씩 감소
    @Modifying
    @Query("update EntryQueue e " +
            "set e.position = e.position - 1 where e.routeId = :routeId")
    void decreasePositionByRouteId(@Param("routeId") Long routeId);

    // Parameter userId 를 가진 Data 탐색
    Optional<EntryQueue> findByUserId(Long userId);

    // Parameter userId 를 가진 Data 삭제
    void deleteAllByUserId(Long userId);
}
