package ewm.client;

import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
@ToString
public class StatCountDto {
    private String app;
    private String uri;
    private long hits;
}
