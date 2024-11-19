package climbing.climbBack.climbingData.controller;

import climbing.climbBack.climbingData.domain.BodyMovementDto;
import climbing.climbBack.climbingData.domain.BodyMovementInputList;
import climbing.climbBack.climbingData.domain.MovementOutputDto;
import climbing.climbBack.climbingData.service.BodyMovementService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/bodyMovement")
@RequiredArgsConstructor
@Slf4j
public class BodyMovementController {

    private final BodyMovementService bodyMovementService;

    @PostMapping("/{userId}/{climbingDataId}")
    @Operation(summary = "등반 패턴 기록", description = "App 에서 분석한 등반 패턴을 기록 한다")
    public ResponseEntity<Map<String, String>> saveClimbingPattern(@RequestBody List<BodyMovementDto> bodyMovementDtoList,
                                                                   @Parameter(description = "등반 패턴을 분석한 App User 의 ID") @PathVariable Long userId, @Parameter(description = "등반 패턴을 기록할 등반 기록의 ID") @PathVariable Long climbingDataId) {

        // 응답용 객체 생성
        Map<String, String> response = new HashMap<>();

        // 등반 패턴 기록
        try {
            bodyMovementService.savePositionList(bodyMovementDtoList, userId, climbingDataId);
        } catch (RuntimeException e) {
            response.put("errorMessage", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        response.put("message", "Climbing Pattern is saved");

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/{climbingDataId}")
    @Operation(summary = "등반 패턴 조회", description = "등반 패턴을 재현할 수 있는 모든 데이터를 제공한다")
    public List<BodyMovementDto> getClimbingPatternData(@PathVariable Long climbingDataId) {

        // 등반 패턴 재현 객체 조회
        return bodyMovementService.getClimbingPattern(climbingDataId);
    }
}
