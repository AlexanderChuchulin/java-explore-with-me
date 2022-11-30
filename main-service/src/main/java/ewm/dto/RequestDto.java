package ewm.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import ewm.abstraction.EntityDto;
import ewm.other.RequestStatus;
import lombok.*;

import java.time.LocalDateTime;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class RequestDto extends EntityDto {
    @JsonProperty("id")
    private Long requestId;
    @JsonProperty("created")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime requestCreated;
    @JsonProperty("event")
    private Long eventId;
    @JsonProperty("requester")
    private Long requesterId;
    @JsonProperty("status")
    RequestStatus requestStatus;
    @JsonProperty("rating")
    private Integer ratingFromRequester;

    @Override
    public Long getId() {
        return getRequestId();
    }
}
