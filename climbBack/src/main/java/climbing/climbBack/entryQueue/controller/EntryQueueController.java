package climbing.climbBack.entryQueue.controller;

import climbing.climbBack.entryQueue.domain.QueueRegisterDto;
import climbing.climbBack.entryQueue.service.EntryQueueService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class EntryQueueController {

    private final EntryQueueService entryQueueService;

    @PostMapping("/entryQueue/register")
    public void registerEntry(@RequestBody QueueRegisterDto registerDto) {

        // UserId & RouteId 추출
        Long userId = registerDto.getUserId();
        Long routeId = registerDto.getRouteId();

        // 대기열 중복 등록 검사
        if (entryQueueService.checkQueueForUser(userId)) {
            log.info("Duplicate user tries to register EntryQueue = {}", userId);
            return;
        }

        // 대기열 등록
        entryQueueService.createEntryQueue(userId, routeId);
    }

    @PostMapping("/entryQueue/{userId}/delete")
    public void deleteEntry(@PathVariable Long userId) {

        // 등록된 entry 가 존재 하는지 확인
        if (!entryQueueService.checkQueueForUser(userId)) {
            log.info("User does not register entry = {}", userId);
            return;
        }

        // 등록된 entry 삭제
        entryQueueService.deleteEntry(userId);
    }
}
