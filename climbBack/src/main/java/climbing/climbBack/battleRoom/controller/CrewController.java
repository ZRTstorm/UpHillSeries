package climbing.climbBack.battleRoom.controller;

import climbing.climbBack.battleRoom.domain.*;
import climbing.climbBack.battleRoom.service.CrewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/crew")
@RequiredArgsConstructor
@Slf4j
public class CrewController {

    private final CrewService crewService;

    // 크루 생성 Controller
    @PostMapping("/{userId}")
    @Operation(summary = "크루 생성", description = "크루 조직을 생성한다")
    public ResponseEntity<?> createCrew(
            @Parameter(description = "요청을 보내는 APP User 의 ID") @PathVariable Long userId,
            @RequestBody CrewCreateDto createDto) {

        // 크루 생성
        Long crewId = crewService.createCrew(userId, createDto);

        // 응답 객체 생성
        HashMap<String, Long> response = new HashMap<>();
        response.put("crewId", crewId);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    // 크루원 등록 Controller
    @PostMapping("/crewMan/{userId}/register")
    @Operation(summary = "크루 가입", description = "User 가 크루에 가입 한다")
    public ResponseEntity<?> registerCrewMan(
            @Parameter(description = "요청을 보내는 APP User 의 ID") @PathVariable Long userId,
            @RequestBody CrewManRegisterDto registerDto) {

        // User 가 이미 크루에 가입 했는지 확인
        if (crewService.checkCrewIn(userId)) {
            log.info("CrewMan Register : User already register in Other Crew = {}", userId);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        // Crew 가입 password 가 일치하는 지 확인
        if (!crewService.checkCrewPassword(registerDto)) {
            log.info("CrewMan Register : User Password is not Acceptable = {}", userId);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("PasswordNot");
        }

        // 크루원 등록
        Long crewManId = crewService.createCrewMan(userId, registerDto.getCrewId());

        // 응답 객체 생성
        Map<String, Long> response = new HashMap<>();
        response.put("crewManId", crewManId);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    // 크루 삭제 Controller
    @DeleteMapping("/{crewId}/{userId}")
    @Operation(summary = "크루 삭제", description = "크루장이 크루를 삭제 한다")
    public ResponseEntity<?> deleteCrew(
            @Parameter(description = "삭제 하고자 하는 Crew 의 ID") @PathVariable Long crewId,
            @Parameter(description = "요청을 보내는 APP User 의 ID") @PathVariable Long userId) {

        // 요청한 User 가 크루장 인지 확인
        if (!crewService.checkCrewPilot(userId, crewId)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        // 크루 & 크루원 삭제
        crewService.deleteCrew(crewId);

        return ResponseEntity.status(HttpStatus.OK).build();
    }

    // 크루 탈퇴 Controller
    @DeleteMapping("/crewMan/{userId}")
    @Operation(summary = "크루 탈퇴", description = "크루원이 크루를 탈퇴 한다")
    public ResponseEntity<?> deleteCrewMan(
            @Parameter(description = "요청을 보내는 APP User 의 ID") @PathVariable Long userId) {

        // 크루 탈퇴
        crewService.deleteCrewMan(userId);

        return ResponseEntity.status(HttpStatus.OK).build();
    }

    // 크루 전체 조회
    @GetMapping("/all")
    @Operation(summary = "크루 전체 조회", description = "등록된 크루를 전부 조회한다")
    public List<CrewSearchDto> searchAllCrew() {
        return crewService.searchAllCrews();
    }

    // 크루 이름 으로 Crew 조회
    @GetMapping("/{crewName}")
    @Operation(summary = "크루 검색", description = "크루 이름으로 크루 검색")
    public List<CrewSearchDto> searchCrewByName(
            @Parameter(description = "검색 하고자 하는 크루 이름") @PathVariable String crewName) {

        return crewService.searchCrewByName(crewName);
    }

    // User ID - Crew Data 조회 Controller
    @GetMapping("/{userId}/crewInfo")
    @Operation(summary = "유저 크루 동기화 조회", description = "유저가 가입한 크루 정보를 조회한다")
    public ResponseEntity<?> searchUserCrewInfo(
            @Parameter(description = "요청을 보내는 APP User 의 ID") @PathVariable Long userId) {

        // User 가 크루에 등록 되어 있는 지 검사
        if (!crewService.checkCrewIn(userId)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User is Not in Crew");
        }

        // User - CrewInfo 조회
        CrewInfoDto crewInfoDto = crewService.searchUserCrewInfo(userId);

        return ResponseEntity.status(HttpStatus.OK).body(crewInfoDto);
    }

    // 크루 - 크루원 전체 조회
    @GetMapping("/{crewId}/crewMan")
    @Operation(summary = "크루원 전체 검색", description = "크루에 속한 크루원을 모두 조회한다")
    public List<CrewManSearchDto> searchAllCrewMans(
            @Parameter(description = "검색 하고자 하는 크루의 ID") @PathVariable Long crewId) {

        return crewService.searchAllCrewMans(crewId);
    }

    // 크루 이미지 삽입
    @PatchMapping("/{crewId}/image")
    @Operation(summary = "크루 이미지 삽입", description = "저장된 크루 이미지를 변경 한다")
    public ResponseEntity<?> patchCrewImage(
            @Parameter(description = "이미지 넣고자 하는 크루의 ID") @PathVariable Long crewId,
            @RequestBody String crewImage) {

        try {
            crewService.putCrewImage(crewId, crewImage);
            return ResponseEntity.status(HttpStatus.OK).build();
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    // 크루 이미지 조회
    @GetMapping("/{crewId}/image")
    @Operation(summary = "크루 이미지 조회", description = "저장된 쿠르 이미지를 조회한다")
    public ResponseEntity<?> getCrewImage(
            @Parameter(description = "검색 하고자 하는 크루의 ID") @PathVariable Long crewId) {

        // CrewImage Optional 조회
        Optional<String> crewImage = crewService.getCrewImage(crewId);

        // 조회 결과가 없다면 400 return
        if (crewImage.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        return ResponseEntity.status(HttpStatus.OK).body(crewImage.get());
    }

}
