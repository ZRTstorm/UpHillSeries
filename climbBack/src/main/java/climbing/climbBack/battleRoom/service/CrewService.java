package climbing.climbBack.battleRoom.service;

import climbing.climbBack.battleRoom.domain.*;
import climbing.climbBack.battleRoom.repository.CrewManRepository;
import climbing.climbBack.battleRoom.repository.CrewRepository;
import climbing.climbBack.user.domain.Users;
import climbing.climbBack.user.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CrewService {

    private final CrewRepository crewRepository;
    private final CrewManRepository crewManRepository;
    private final UsersRepository usersRepository;

    // 크루 생성 서비스
    @Transactional
    public Long createCrew(Long userId, CrewCreateDto createDto) {
        // 크루 생성
        Crew crew = new Crew();

        crew.setCrewName(createDto.getCrewName());
        crew.setContent(createDto.getContent());
        crew.setAdminUser(usersRepository.getReferenceById(userId));

        // Crew Password 등록
        crew.setPassword(createDto.getPassword());

        Crew savedCrew = crewRepository.save(crew);

        // 생성된 크루 -> 크루장 크루원 등록
        createCrewMan(userId, savedCrew.getId());

        return savedCrew.getId();
    }

    // 크루원 등록 서비스
    @Transactional
    public Long createCrewMan(Long userId, Long crewId) {
        // 크루원 등록
        CrewMan crewMan = new CrewMan();

        crewMan.setCrew(crewRepository.getReferenceById(crewId));
        crewMan.setUsers(usersRepository.getReferenceById(userId));

        CrewMan savedMan = crewManRepository.save(crewMan);
        return savedMan.getId();
    }

    // User 가 크루에 등록 되어 있는지 아닌지 확인 서비스
    @Transactional(readOnly = true)
    public boolean checkCrewIn(Long userId) {
        return crewManRepository.existsByUserId(userId);
    }

    // User 가 크루장 인지 확인 서비스
    @Transactional(readOnly = true)
    public boolean checkCrewPilot(Long userId, Long crewId) {
        // Crew 획득
        Optional<Crew> crewOpt = crewRepository.findById(crewId);

        if (crewOpt.isEmpty()) {
            throw new IllegalStateException("Crew is not in DB");
        }

        Crew crew = crewOpt.get();
        return Objects.equals(crew.getAdminUser().getId(), userId);
    }

    // Crew password 가 일치하는지 확인
    @Transactional(readOnly = true)
    public boolean checkCrewPassword(CrewManRegisterDto registerDto) {
        // Crew 조회
        Optional<Crew> crewOpt = crewRepository.findById(registerDto.getCrewId());
        if (crewOpt.isEmpty()) return false;

        // Crew Password 와 입력 Password 가 일치 하는지 확인
        Crew crew = crewOpt.get();
        return Objects.equals(crew.getPassword(), registerDto.getPassword());
    }

    // User - CrewInfo 조회 서비스
    @Transactional(readOnly = true)
    public CrewInfoDto searchUserCrewInfo(Long userId) {
        CrewInfoDto crewInfoDto = new CrewInfoDto();

        // user 가 등록된 Crew ID 조회
        Long crewId = crewManRepository.findCrewIdByUserId(userId);

        // Crew 조회
        Optional<Crew> crewOpt = crewRepository.findById(crewId);
        if (crewOpt.isEmpty()) throw new IllegalStateException("NotCrew");

        Crew crew = crewOpt.get();
        crewInfoDto.setCrewId(crewId);
        crewInfoDto.setCrewName(crew.getCrewName());
        crewInfoDto.setCrewContent(crew.getContent());
        crewInfoDto.setPassword(crew.getPassword());

        // User Info 조회
        Optional<Users> userOpt = usersRepository.findById(userId);
        if (userOpt.isEmpty()) throw new IllegalStateException("NotUser");

        crewInfoDto.setPilotId(userId);
        crewInfoDto.setPilotName(userOpt.get().getNickname());

        // crewMan List 조회
        List<CrewManSearchDto> crewManDtoList = searchAllCrewMans(crewId);
        crewInfoDto.setCrewManList(crewManDtoList);

        return crewInfoDto;
    }

    // Crew 삭제 서비스
    @Transactional
    public void deleteCrew(Long crewId) {
        // Crew 에 속한 CrewMan 전부 삭제
        crewManRepository.deleteAllFromCrew(crewId);

        // Crew 삭제
        crewRepository.deleteById(crewId);
    }

    // Crew 탈퇴 서비스
    @Transactional
    public void deleteCrewMan(Long userId) {
        crewManRepository.deleteByUserId(userId);
    }

    // User 가 가입한 Crew ID 조회 서비스
    @Transactional(readOnly = true)
    public Long searchUserInCrew(Long userId) {
        // User 가 가입한 Crew ID 조회
        return crewManRepository.findCrewIdByUserId(userId);
    }

    // Crew 전체 조회 서비스
    @Transactional(readOnly = true)
    public List<CrewSearchDto> searchAllCrews() {
        return crewRepository.findAllCrew();
    }

    // CrewName 으로 Crew 조회 서비스
    @Transactional(readOnly = true)
    public List<CrewSearchDto> searchCrewByName(String crewName) {
        // Crew 이름 Crew 조회
        return crewRepository.findCrewByName(crewName);
    }

    // 크루원 전체 조회 서비스
    @Transactional(readOnly = true)
    public List<CrewManSearchDto> searchAllCrewMans(Long crewId) {
        return crewManRepository.findAllCrewMans(crewId);
    }

    // 크루 이미지 삽입 서비스
    @Transactional
    public void putCrewImage(Long crewId, String crewImage) {
        // crew 검색
        Optional<Crew> crewOpt = crewRepository.findById(crewId);

        if (crewOpt.isEmpty()) {
            throw new IllegalStateException("Crew is not in DB");
        }

        Crew crew = crewOpt.get();
        crew.setCrewIcon(crewImage);
    }

    // 크루 이미지 조회 서비스
    @Transactional(readOnly = true)
    public Optional<String> getCrewImage(Long crewId) {
        return crewRepository.findCrewImage(crewId);
    }
}
