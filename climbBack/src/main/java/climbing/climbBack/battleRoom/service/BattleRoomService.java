package climbing.climbBack.battleRoom.service;

import climbing.climbBack.battleRoom.domain.BattleCreateDto;
import climbing.climbBack.battleRoom.domain.BattleRoom;
import climbing.climbBack.battleRoom.domain.BattleSearchDto;
import climbing.climbBack.battleRoom.domain.Participant;
import climbing.climbBack.battleRoom.repository.BattleRoomRepository;
import climbing.climbBack.battleRoom.repository.CrewManRepository;
import climbing.climbBack.battleRoom.repository.ParticipantRepository;
import climbing.climbBack.route.repository.RouteRepository;
import climbing.climbBack.user.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class BattleRoomService {

    private final BattleRoomRepository battleRoomRepository;
    private final RouteRepository routeRepository;
    private final UsersRepository usersRepository;

    private final CrewManRepository crewManRepository;
    private final ParticipantRepository participantRepository;

    // BattleRoom 생성 서비스
    @Transactional
    public BattleRoom createBattleRoom(BattleCreateDto battleCreateDto, Long userId) {
        // BattleRoom 생성
        BattleRoom battleRoom = new BattleRoom();

        // 내용 추출
        battleRoom.setTitle(battleCreateDto.getTitle());
        battleRoom.setContent(battleCreateDto.getContent());

        // crew ID 입력
        // crew 에 들어가 있지 않다면 0 , crew 에 들어가 있다면 crewId
        if (!battleCreateDto.getCrewOpen()) {
            battleRoom.setCrewId(0L);
        } else {
            // User 의 Crew ID 확인
            Long crewId = crewManRepository.findCrewIdByUserId(userId);
            battleRoom.setCrewId(crewId);
        }

        // ClimbingCenter 내부에 있는 Route 중 선택 -> 직접 기입은 지양 , 선택 지향
        // 프록시 인스턴스 참조
        battleRoom.setRoute(routeRepository.getReferenceById(battleCreateDto.getRouteId()));
        battleRoom.setAdminUser(usersRepository.getReferenceById(userId));

        // 방 초기 설정
        battleRoom.setProgress(true);
        battleRoom.setStartTime(LocalDateTime.now());

        // UUID 생성
        String uuid = UUID.randomUUID().toString();
        String shortUUID = uuid.replace("-", "").substring(0, 8);
        battleRoom.setParticipantCode(shortUUID);

        return battleRoomRepository.save(battleRoom);
    }

    // BattleRoom 삭제 서비스
    // BattleRoom 삭제 -> Battle 참여 Participants 삭제
    @Transactional
    public void deleteBattleRoom(Long battleRoomId) {
        // battleRoom ID 와 Mapping 되는 BattleRoom 삭제
        battleRoomRepository.deleteById(battleRoomId);

        // Battle 에 참여한 모든 Participant Data 삭제
        participantRepository.deleteAllByBattleRoomId(battleRoomId);
    }

    // Battle 종료 처리 서비스
    @Transactional
    public void endingBattleRoom(Long battleRoomId) {
        // battleRoom 획득
        Optional<BattleRoom> battleRoomOpt = battleRoomRepository.findById(battleRoomId);

        if (battleRoomOpt.isEmpty()) return;

        BattleRoom battleRoom = battleRoomOpt.get();
        battleRoom.setProgress(false);
    }

    // BattleRoom 주인 확인 서비스
    @Transactional(readOnly = true)
    public Boolean checkBattleRoomAdmin(Long battleRoomId, Long userId) {
        // battleRoom 획득
        Optional<BattleRoom> battleRoomOpt = battleRoomRepository.findById(battleRoomId);

        // BattleRoom 이 존재 하지 않으면 false return
        if (battleRoomOpt.isEmpty()) return false;

        // battleRoom 의 주인이 User 가 아니면 false return
        BattleRoom battleRoom = battleRoomOpt.get();
        return Objects.equals(battleRoom.getAdminUser().getId(), userId);
    }

    // 참여 코드를 사용 BattleRoom 조회 서비스
    @Transactional(readOnly = true)
    public BattleSearchDto searchBattleForCode(String participantCode) {
        // 참여 코드 -> BattleRoom 조회
        Optional<BattleRoom> battleRoomOpt = battleRoomRepository.searchRoomForCode(participantCode);

        // 조회 결과가 존재 하는지 확인
        if (battleRoomOpt.isEmpty()) {
            log.info("Search Room for Code : Matching Room is empty = {}", participantCode);
            throw new IllegalStateException("Search Room for Code : Matching Room is empty");
        }
        BattleRoom battleRoom = battleRoomOpt.get();

        // BattleSearchDto 구성
        BattleSearchDto searchDto = new BattleSearchDto();

        searchDto.setBattleRoomId(battleRoom.getId());
        searchDto.setTitle(battleRoom.getTitle());
        searchDto.setAdminName(battleRoom.getAdminUser().getNickname());
        searchDto.setRouteId(battleRoom.getRoute().getId());
        searchDto.setProgress(battleRoom.getProgress());

        return searchDto;
    }

    // User 가 참여한 모든 BattleRoom 조회 서비스
    @Transactional(readOnly = true)
    public List<BattleSearchDto> searchAllBattleForUser(Long userId) {
        return participantRepository.findAllBattleByUser(userId);
    }

    // Crew 에서 공유 하는 모든 BattleRoom 조회 Controller
    @Transactional(readOnly = true)
    public List<BattleSearchDto> searchAllBattleForCrew(Long crewId) {
        // Crew 에 공개한 모든 BattleRoom 조회
        List<BattleRoom> allBattle = battleRoomRepository.findAllRoomByCrew(crewId);

        return allBattle.stream()
                .filter(BattleRoom::getProgress)
                .map(battleRoom -> new BattleSearchDto(
                        battleRoom.getId(),
                        battleRoom.getTitle(),
                        battleRoom.getAdminUser().getNickname(),
                        battleRoom.getRoute().getId(),
                        true
                )).toList();
    }

    // 참가 신청 서비스
    @Transactional
    public void participantBattle(Long userId, Long battleRoomId) {
        // 현재 Battle 이 진행 중인지 확인
        Optional<BattleRoom> battleRoomOpt = battleRoomRepository.findById(battleRoomId);

        if (battleRoomOpt.isEmpty()) {
            throw new IllegalStateException("Participant Error: Battle is not Opened");
        }

        BattleRoom battleRoom = battleRoomOpt.get();
        if (!battleRoom.getProgress()) {
            throw new IllegalStateException("Participant Error : Battle is Over");
        }

        // 참가자 생성
        Participant participant = new Participant();

        participant.setUsers(usersRepository.getReferenceById(userId));
        participant.setBattleRoom(battleRoom);

        participantRepository.save(participant);
    }
}
