package climbing.climbBack.climbingData.service;

import climbing.climbBack.climbingData.domain.BodyMovement;
import climbing.climbBack.climbingData.domain.BodyMovementDto;
import climbing.climbBack.climbingData.domain.ClimbingData;
import climbing.climbBack.climbingData.domain.MovementOutputDto;
import climbing.climbBack.climbingData.repository.BodyMovementRepository;
import climbing.climbBack.climbingData.repository.ClimbingDataRepository;
import climbing.climbBack.route.domain.Route;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class BodyMovementService {

    private final BodyMovementRepository bodyMovementRepository;
    private final ClimbingDataRepository climbingDataRepository;

    @Transactional
    public void savePositionList(List<BodyMovementDto> dtoList, Long userId, Long climbingDataId) {
        // climbingData 프록시 객체 참조 획득
        ClimbingData climbingData = climbingDataRepository.getReferenceById(climbingDataId);

        // dtoList -> BodyMovement List 변환 작업
        List<BodyMovement> movementList = new ArrayList<>();

        for (BodyMovementDto dto : dtoList) {
            BodyMovement item = new BodyMovement();

            item.setSequence(dto.getSequence());
            item.setXPos(dto.getXPos());
            item.setYPos(dto.getYPos());

            // ClimbingData 할당
            item.setClimbingData(climbingData);

            movementList.add(item);
        }

        // BodyMovement List 저장
        bodyMovementRepository.saveAll(movementList);
    }

    // BodyMovement Data 에 ClimbingData 를 Mapping
    // Mapping 해서 만든 BodyMovement Entity List 를 DB 에 저장
    @Transactional
    public void savePositionListPrevious(List<BodyMovementDto> dtoList, Long userId) {
        // userId 와 일치 하는 Data 중 가장 최근에 만들어진 등반 기록
        Optional<ClimbingData> data = climbingDataRepository.findTopByUserId(userId);

        // 사용자가 등반 기록을 생성한 적이 있는지 검사
        if (data.isEmpty()) {
            log.info("User does not make ClimbingData : userId = {}", userId);
            throw new IllegalStateException("User does not make ClimbingData : userId = " + userId);
        }

        // 등반 기록 추출
        ClimbingData climbingData = data.get();

        // Matching 되는 등반 기록이 있는지 검사
        if (bodyMovementRepository.existsByClimbingDataId(climbingData.getId())) {
            log.info("ClimbingData already have pattern Data List : climbingData = {}", climbingData.getId());
            throw new IllegalStateException("ClimbingData already have pattern Data List");
        }

        // dtoList -> BodyMovement List 변환 작업
        List<BodyMovement> movementList = new ArrayList<>();

        for (BodyMovementDto dto : dtoList) {
            BodyMovement item = new BodyMovement();

            item.setSequence(dto.getSequence());
            item.setXPos(dto.getXPos());
            item.setYPos(dto.getYPos());

            // ClimbingData 할당
            item.setClimbingData(climbingData);

            movementList.add(item);
        }

        // BodyMovement List 저장
        bodyMovementRepository.saveAll(movementList);
    }

    // 등반 기록에 저장된 등반 패턴 기록 조회
    // { 루트 이미지 , 시작 홀드와 탑 홀드 좌표값 , 등반 패턴 좌표값 }
    @Transactional(readOnly = true)
    public MovementOutputDto getClimbingPattern(Long climbingId) {
        // ClimbingDataId 와 Mapping 되는 모든 BodyMovement Data 조회
        List<BodyMovement> dataList = bodyMovementRepository.findByClimbingDataId(climbingId);

        // Movement Data 가 채워져 있는지 검사
        if (dataList.isEmpty()) {
            log.info("ClimbingData does not have any position Data : ID = {}", climbingId);
            throw new IllegalStateException("ClimbingData does not have any position Data : ID = " + climbingId);
        }

        // climbingData 의 route 정보 획득
        Optional<ClimbingData> climbingOpt = climbingDataRepository.findById(climbingId);
        if (climbingOpt.isEmpty()) throw new IllegalStateException("ClimbingPattern : ClimbingData Error");

        // climbingData 의 루트 정보 획득
        Route route = climbingOpt.get().getRoute();

        // BodyMovement List 를 BodyMovementDto List 로 변환
        List<BodyMovementDto> dtoList = dataList.stream()
                .map(bodyMovement -> new BodyMovementDto(
                        bodyMovement.getSequence(),
                        bodyMovement.getXPos(),
                        bodyMovement.getYPos()
                )).toList();

        // 응답 객체 채우기
        MovementOutputDto output = new MovementOutputDto();

        // 응답 객체 -> imageURL 채우기
        // 응답 객체 -> 시작 홀드 , 탑 홀드 좌표값 채우기
        // 응답 객체 -> 좌표 리스트 채우기
        output.setStartX(route.getStartX());
        output.setStartY(route.getStartY());
        output.setEndX(route.getEndX());
        output.setEndY(route.getEndY());
        output.setMovements(dtoList);

        return output;
    }

}
