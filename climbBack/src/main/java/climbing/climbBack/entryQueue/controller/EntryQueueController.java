package climbing.climbBack.entryQueue.controller;

import climbing.climbBack.entryQueue.domain.EntryCountDto;
import climbing.climbBack.entryQueue.domain.EntryQueue;
import climbing.climbBack.entryQueue.domain.QueueRegisterDto;
import climbing.climbBack.entryQueue.service.EntryQueueService;
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

@RestController
@RequestMapping("/entryQueue")
@RequiredArgsConstructor
@Slf4j
public class EntryQueueController {

    private final EntryQueueService entryQueueService;

    // 대기열 등록 Controller
    @PostMapping("/register")
    @Operation(summary = "대기열 등록", description = "User ID 와 Route ID 를 받아서 대기열을 생성한다")
    public ResponseEntity<Map<String, String>> registerEntry(@RequestBody QueueRegisterDto registerDto) {

        // 응답 용 객체
        Map<String, String> response = new HashMap<>();

        // userId & routeId 추출
        Long userId = registerDto.getUserId();
        Long routeId = registerDto.getRouteId();

        // 대기열 중복 등록 검사
        if (entryQueueService.checkQueueForUser(userId)) {
            log.info("Register Entry - User can register only once : userID = {}, routeID = {}", userId, routeId);
            response.put("errorMessage", "Register Entry - User can register only once");

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        // 대기열 등록
        try {
            entryQueueService.createEntryQueue(userId, routeId);
        } catch (RuntimeException e) {
            log.info("Register Entry - {}", e.getMessage());
            response.put("errorMessage", "Register Entry - " + e.getMessage());

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        response.put("message", "EntryQueue is created Successfully");

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    // 대기열 삭제 Controller
    // 사용자가 APP 에서 임의로 대기열을 취소 하는 케이스
    @DeleteMapping("/{userId}")
    @Operation(summary = "대기열 삭제", description = "사용자가 임의로 대기열을 취소한다")
    public ResponseEntity<Void> deleteEntry(
            @Parameter(description = "요청을 보내는 App User 의 ID") @PathVariable Long userId) {

        // 등록된 entry 가 존재 하는지 확인
        if (!entryQueueService.checkQueueForUser(userId)) {
            log.info("Delete Entry - User did not register entry : userID = {}", userId);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        // 등록된 entry 삭제
        try {
            entryQueueService.deleteEntry(userId);
        } catch (RuntimeException e) {
            log.info("Delete Entry - {}", e.getMessage());

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        return ResponseEntity.status(HttpStatus.OK).build();
    }

    // 이용 요청 거절 Controller
    // 이용 허가 메시지 -> 이용 거부 & 타임 아웃
    @PostMapping("/{userId}/reject")
    @Operation(summary = "루트 이용 거절", description = "이용 요청에 대해 거절 하거나 타임 아웃이 되다")
    public ResponseEntity<?> rejectEntry(
            @Parameter(description = "요청을 보내는 App User 의 ID") @PathVariable Long userId) {

        // User 가 이용 예정인 Route 의 ID 획득
        Long routeId = entryQueueService.getRouteByUserMap(userId);

        // 이용자 HashMap 데이터가 존재 하는지 확인
        if (routeId == -1L) {
            log.info("Reject Entry - User did not in Route : userID = {}", userId);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Reject Entry - User did not in Route");
        }

        // 대기열 정보 삭제 및 대기열 조정
        entryQueueService.manipulateEntryQueue(routeId);

        return ResponseEntity.status(HttpStatus.OK).build();
    }

    // 전체 대기열 조회 Controller
    // API Test 할 때 사용 ( 실 사용은 하지 않을 것 )
    @GetMapping("/data")
    @Operation(summary = "전체 대기열 엔티티 조회", description = "현재 DB 에 등록된 모든 대기열을 조회한다")
    public List<EntryQueue> getAllEntry() {

        return entryQueueService.getAllEntryData();
    }

    // 전체 대기열 COUNT List 조회 Controller
    @GetMapping("/count")
    @Operation(summary = "전체 대기열 COUNT 조회", description = "모든 루트의 대기열 COUNT 를 조회한다")
    public List<EntryCountDto> getAllEntryCount() {

        return entryQueueService.getAllEntryCountList();
    }

    // 암장의 전체 대기열 COUNT List 조회 Controller
    @GetMapping("/{centerId}/count")
    @Operation(summary = "암장의 루트 별 대기열 COUNT 조회", description = "암장에 존재하는 모든 루트의 대기열 COUNT 를 조회한다")
    public List<EntryCountDto> getAllCenterCount(
            @Parameter(description = "조회 하려는 ClimbingCenter 의 ID") @PathVariable Long centerId) {

        return entryQueueService.getCenterCount(centerId);
    }

    // 특정 루트에 대한 대기 인원 조회 Controller
    // QR 코드 스캔 -> 대기열 신청 전 대기 인원 확인 시 요청
    @GetMapping("/{routeId}/count")
    @Operation(summary = "루트 대기 인원 조회", description = "요청한 루트에 대한 대기 인원을 조회한다")
    public EntryCountDto getRouteEntryCount(
            @Parameter(description = "확인 하려는 루트의 ID") @PathVariable Long routeId) {

        return entryQueueService.getRouteEntryCountOne(routeId);
    }

    // 사용자가 신청한 루트의 현재 위치 순서 조회 Controller
    // 대기열 상황 최신화 할 때 요청
    @GetMapping("/{userId}/position")
    @Operation(summary = "사용자의 현재 위치 순서 조회", description = "사용자가 신청한 루트에 대한 현재 위치 순서를 조회한다")
    public EntryCountDto getUserEntryCount(
            @Parameter(description = "요청을 보내는 App User 의 ID") @PathVariable Long userId) {

        return entryQueueService.getUserEntryCountOne(userId);
    }
}
