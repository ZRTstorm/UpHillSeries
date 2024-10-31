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
            @Parameter(description = "요청을 보내는 App User 의 ID")  @PathVariable Long userId) {

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

    // 전체 대기열 조회 Controller
    // API Test 할 때 사용 ( 실 사용은 하지 않을 것 )
    @GetMapping("/data")
    @Operation(summary = "전체 대기열 엔티티 조회", description = "현재 DB 에 등록된 모든 대기열을 조회한다")
    public List<EntryQueue> getAllEntry() {

        return entryQueueService.getAllEntryData();
    }

    // 전체 대기열 COUNT List 조회 Controller
    // 암장의 전체 대기열 상황 조회 시 요청
    @GetMapping("/count")
    @Operation(summary = "전체 대기열 COUNT 조회", description = "모든 루트의 대기열 COUNT 를 조회한다")
    public List<EntryCountDto> getAllEntryCount() {

        return entryQueueService.getAllEntryCountList();
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
