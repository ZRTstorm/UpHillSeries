package climbing.climbBack.climbingData.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class BodyMovementInputList {

    List<BodyMovementDto> bodyMovementDtoList = new ArrayList<>();
}
