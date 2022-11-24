package ewm.statistics;

import com.fasterxml.jackson.annotation.*;

import javax.validation.constraints.NotBlank;

import lombok.*;

import java.time.LocalDateTime;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder(value = {"id", "app", "uri", "ip", "timestamp", "hits"})
public class StatDto {
    @JsonProperty("id")
    private Long hitId;
    @NotBlank
    private String app;
    @NotBlank
    private String uri;
    @NotBlank
    private String ip;
    @JsonProperty("timestamp")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime hitTimestamp;
}
