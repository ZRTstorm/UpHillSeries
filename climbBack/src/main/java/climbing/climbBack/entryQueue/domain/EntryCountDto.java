package climbing.climbBack.entryQueue.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
public class EntryCountDto {

    private Long routeId;
    private Long count;
}
