package ewm.dto;

import com.fasterxml.jackson.annotation.*;
import ewm.abstraction.EntityDto;
import ewm.other.EventStatus;
import lombok.*;

import java.time.LocalDateTime;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode(callSuper = false)
public class EventDto extends EntityDto {
    @JsonProperty("id")
    @JsonAlias("eventId")
    private Long eventId;
    private String annotation;
    @JsonProperty("category")
    private Long categoryId;
    private CategoryDto categoryDto;
    private Long confirmedRequests;
    private String description;
    @JsonProperty("createdOn")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventCreated = LocalDateTime.now();
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventDate;
    @JsonProperty("state")
    private EventStatus eventStatus;
    @JsonProperty("title")
    private String eventTitle;
    private UserShortDto initiator;
    @JsonProperty("location")
    EventLocationDto eventLocationDto;
    private Boolean paid;
    private Integer participantLimit;
    @JsonProperty("publishedOn")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime published;
    private Boolean requestModeration;
    Long views;

    @Override
    public Long getId() {
        return getEventId();
    }

    @JsonProperty("category")
    public CategoryDto getCategoryDto() {
        return categoryDto;
    }

    @JsonInclude
    @Getter
    @AllArgsConstructor
    @ToString
    public static class CategoryDto {
        private long id;
        private String name;
    }

    @JsonInclude
    @Getter
    @AllArgsConstructor
    @ToString
    public static class EventLocationDto {
        private Double lat;
        private Double lon;
    }

    @JsonInclude
    @Getter
    @AllArgsConstructor
    @ToString
    public static class UserShortDto {
        private long id;
        private String name;
    }
}