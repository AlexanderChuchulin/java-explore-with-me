package ewm.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import ewm.abstraction.EntityDto;
import lombok.*;

import java.util.List;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class CompilationDto extends EntityDto {
    @JsonProperty("id")
    private Long compilationId;
    @JsonProperty("events")
    private List<Long> eventIds;
    private List<EventDto> eventDtoList;
    Boolean pinned;
    @JsonProperty("title")
    String compilationTitle;

    @Override
    public Long getId() {
        return compilationId;
    }

    @JsonProperty("events")
    public List<EventDto> getEventDtoList() {
        return eventDtoList;
    }
}
