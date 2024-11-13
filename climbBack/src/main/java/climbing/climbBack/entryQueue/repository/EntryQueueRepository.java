package climbing.climbBack.entryQueue.repository;

import climbing.climbBack.entryQueue.domain.EntryCountDto;
import climbing.climbBack.entryQueue.domain.EntryQueue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


import java.util.List;
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

    // 주어진 pos 값보다 더 큰 pos 값을 가진 Data 의 pos 값을 1씩 감소
    @Modifying
    @Query("update EntryQueue e set e.position = e.position - 1 " +
            "where e.routeId = :routeId and e.position > :position")
    void decreasePositionGreater(@Param("routeId") Long routeId, @Param("position") Long position);

    // Parameter userId 를 가진 Data 탐색
    Optional<EntryQueue> findByUserId(Long userId);

    // Parameter userId 를 가진 Data 삭제
    void deleteAllByUserId(Long userId);

    // routeList 에 포함된 route 중에서 pos == 1 인 Data 만 필터링
    // createdTime 을 기준 으로 오름 차순 정렬
    @Query("select e from EntryQueue e " +
            "where e.routeId in :routeList and e.position = 1 " +
            "order by e.createdTime asc")
    List<EntryQueue> findEntryListOrderedByCreatedTime(@Param("routeList") List<Long> routeList);

    // routeId 로 Group By 하여 모든 route 의 대기열 COUNT 조회
    @Query("select new climbing.climbBack.entryQueue.domain.EntryCountDto(e.routeId, count(e)) " +
            "from EntryQueue e " +
            "group by e.routeId order by e.routeId asc")
    List<EntryCountDto> countAllEntryByRoute();

    // routeId 가 일치 하는 Data 의 개수 조회
    // routeId 로 등록된 Data 가 없는 경우 Optional.empty() 반환
    @Query("select new climbing.climbBack.entryQueue.domain.EntryCountDto(e.routeId, count(e)) " +
            "from EntryQueue  e " +
            "where e.routeId = :routeId")
    Optional<EntryCountDto> countRouteEntry(@Param("routeId") Long routeId);

    // userId 와 Matching 되는 Data 의 { routeId : position } 값을 조회
    @Query("select new climbing.climbBack.entryQueue.domain.EntryCountDto(e.routeId, e.position) " +
            "from EntryQueue e " +
            "where e.userId = :userId")
    Optional<EntryCountDto> findPositionRouteByUserId(@Param("userId") Long userId);

    @Query("select e.routeId from EntryQueue e where e.userId = :userId")
    Long findRouteIdByUserId(@Param("userId") Long routeId);

    @Query("select new climbing.climbBack.entryQueue.domain.EntryCountDto(r.id, count(e)) " +
            "from Route r left join EntryQueue e on r.id = e.routeId " +
            "where r.climbingCenter.id = :climbingCenterId " +
            "group by r.id")
    List<EntryCountDto> countEntryByClimbingCenter(@Param("climbingCenterId") Long climbingCenterId);
}
