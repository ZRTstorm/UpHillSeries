package climbing.climbBack.battleRoom.controller;

import climbing.climbBack.battleRoom.domain.*;
import climbing.climbBack.battleRoom.service.BattleRoomService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/battleRoom")
@RequiredArgsConstructor
public class BattleRoomController {

    private final BattleRoomService battleRoomService;

    // BattleRoom 생성 Controller
    @PostMapping("/{userId}/registry")
    @Operation(summary = "배틀룸 생성", description = "배틀 설정을 입력 받아서 배틀룸을 생성한다")
    public ResponseEntity<?> registryBattleRoom(
            @Valid @RequestBody BattleCreateDto createDto,
            @Parameter(description = "배틀룸을 생성 하는 user ID") @PathVariable Long userId) {

        // BattleRoom 생성
        BattleRoom battleRoom = battleRoomService.createBattleRoom(createDto, userId);

        // ParticipantCode & BattleRoom ID 반환
        BattleRegisterDto responseDto = new BattleRegisterDto();
        responseDto.setBattleRoomId(battleRoom.getId());
        responseDto.setParticipantCode(battleRoom.getParticipantCode());

        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    // BattleRoom 삭제 Controller
    @DeleteMapping("/{userId}/{battleRoomId}")
    @Operation(summary = "배틀룸 삭제", description = "배틀룸을 삭제한다")
    public ResponseEntity<?> deleteBattleRoom(
            @Parameter(description = "삭제 하고자 하는 배틀룸 ID") @PathVariable Long battleRoomId,
            @Parameter(description = "요청을 보내는 APP User 의 ID") @PathVariable Long userId) {

        // 요청한 User 가 BattleRoom 의 주인 인지 확인
        if (!battleRoomService.checkBattleRoomAdmin(battleRoomId, userId)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        // BattleRoom & 참여한 모든 Participant 삭제
        battleRoomService.deleteBattleRoom(battleRoomId);

        return ResponseEntity.status(HttpStatus.OK).build();
    }

    // BattleRoom 완료 Controller
    @PatchMapping("/{userId}/{battleRoomId}/end")
    @Operation(summary = "배틀 종료", description = "배틀룸 상태를 배틀 종료로 전환 한다")
    public ResponseEntity<?> endBattleRoom(
            @Parameter(description = "완료 하고자 하는 배틀룸 ID") @PathVariable Long battleRoomId,
            @Parameter(description = "요청을 보내는 APP User 의 ID") @PathVariable Long userId) {

        // 요청한 User 가 BattleRoom 의 주인 인지 확인
        if (!battleRoomService.checkBattleRoomAdmin(battleRoomId, userId)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        // BattleRoom 상태 완료 조정
        battleRoomService.endingBattleRoom(battleRoomId);

        return ResponseEntity.status(HttpStatus.OK).build();
    }

    // 참여 코드 사용 BattleRoom 조회 Controller
    @GetMapping("/participant/{participantCode}")
    @Operation(summary = "참여 코드 조회", description = "참여 코드를 사용 하여 배틀룸을 조회한다")
    public ResponseEntity<?> getRoomForCode(
            @Parameter(description = "검색을 위해 입력한 참여 코드") @PathVariable String participantCode) {

        // SearchDto = { battleRoomID , Title , adminName , routeId }
        try {
            BattleSearchDto searchDto = battleRoomService.searchBattleForCode(participantCode);
            return ResponseEntity.status(HttpStatus.OK).body(searchDto);
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    // User 가 참여한 모든 BattleRoom 조회 Controller
    @GetMapping("/users/{userId}/all")
    @Operation(summary = "User 참여 대회 전체 조회", description = "User 가 참여 했던 모든 배틀룸을 조회한다")
    public List<BattleSearchDto> getAllRoomForUser(
            @Parameter(description = "요청을 보내는 APP User 의 ID") @PathVariable Long userId) {

        return battleRoomService.searchAllBattleForUser(userId);
    }

    // Crew 에서 공유 하는 모든 BattleRoom 조회 Controller
    @GetMapping("/crews/{crewId}/all")
    @Operation(summary = "Crew 대회 전체 조회", description = "Crew 에 공개한 모든 배틀룸을 조회한다")
    public List<BattleSearchDto> getAllRoomForCrew(
            @Parameter(description = "요청을 보내는 APP User 의 Crew ID") @PathVariable Long crewId) {

        return battleRoomService.searchAllBattleForCrew(crewId);
    }

    // BattleRoom 참가 신청 Controller
    @PostMapping("/{userId}/{battleRoomId}/participant")
    @Operation(summary = "배틀 참가 신청", description = "배틀룸 ID 와 일치 하는 배틀에 참가를 신청한다")
    public ResponseEntity<?> enterBattleRoom(
            @Parameter(description = "요청을 보내는 APP User 의 ID") @PathVariable Long userId,
            @Parameter(description = "참가 하고자 하는 배틀룸 ID") @PathVariable Long battleRoomId) {

        // 참가 신청
        // Battle 이 존재 하지 않거나 이미 종료된 Battle 이면 Exception
        try {
            battleRoomService.participantBattle(userId, battleRoomId);
            return ResponseEntity.status(HttpStatus.OK).build();
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    // BattleRoom 기본 정보 조회 Controller
    @GetMapping("/{battleRoomId}/info")
    @Operation(summary = "배틀룸 조회", description = "배틀룸 기본 정보를 조회 한다")
    public BattleSearchDto infoBattleRoom(
            @Parameter(description = "조회 하고자 하는 배틀룸 ID") @PathVariable Long battleRoomId) {

        return battleRoomService.battleInfo(battleRoomId);
    }

    // Participant - ClimbingData 삽입 Controller
    @PostMapping("/{userId}/{battleRoomId}/{climbingDataId}")
    @Operation(summary = "등반 기록 대회 등록", description = "등반 기록을 대회에 등록 한다")
    public ResponseEntity<?> entryBattle(
            @Parameter(description = "요청을 보내는 APP User 의 ID") @PathVariable Long userId,
            @Parameter(description = "조회 하고자 하는 배틀룸 ID") @PathVariable Long battleRoomId,
            @Parameter(description = "등록 하고자 하는 등반 기록 ID") @PathVariable Long climbingDataId) {

        try {
            battleRoomService.entryBattle(battleRoomId, userId, climbingDataId);
            return ResponseEntity.status(HttpStatus.OK).build();
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    // BattleRoom 에 등록된 모든 ClimbingData 조회 Controller
    @GetMapping("/{battleRoomId}/climbingData")
    @Operation(summary = "배틀 대회 기록 조회", description = "배틀에 등록된 모든 등반 기록을 조회 한다")
    public List<BattleDataDto> getBattleData(
            @Parameter(description = "조회 하고자 하는 배틀룸 ID") @PathVariable Long battleRoomId) {

        return battleRoomService.getAllBattleData(battleRoomId);
    }
}
