package ewm.statistics;

import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
public class StatCountDto {
    private String app;
    private String uri;
    private long hits;
}
