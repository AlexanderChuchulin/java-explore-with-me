package ewm.mapper;

import ewm.abstraction.EntityMapper;
import ewm.dto.EventDto;
import ewm.model.Event;
import ewm.other.DtoType;
import ewm.other.EventStatus;
import ewm.repository.CategoryJpaRepository;
import ewm.repository.UserJpaRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class EventMapper extends EntityMapper<EventDto, Event> {
    private final CategoryJpaRepository categoryJpaRepository;
    private final UserJpaRepository userJpaRepository;

    public EventMapper(CategoryJpaRepository categoryJpaRepository, UserJpaRepository userJpaRepository) {
        this.categoryJpaRepository = categoryJpaRepository;
        this.userJpaRepository = userJpaRepository;
    }

    @Override
    public Event dtoToEntity(EventDto eventDto, Long... params) {
        return Event.builder()
                .eventId(eventDto.getEventId())
                .annotation(eventDto.getAnnotation())
                .category(categoryJpaRepository.findById(eventDto.getCategoryId()).orElseThrow())
                .confirmedRequests(0L)
                .description(eventDto.getDescription())
                .eventCreated(LocalDateTime.now())
                .eventDate(eventDto.getEventDate())
                .eventLatitude(eventDto.getEventLocationDto() != null ? eventDto.getEventLocationDto().getLat() : null)
                .eventLongitude(eventDto.getEventLocationDto() != null ? eventDto.getEventLocationDto().getLon() : null)
                .eventStatus(EventStatus.PENDING)
                .eventTitle(eventDto.getEventTitle())
                .initiator(params.length > 0 ? userJpaRepository.findById(params[0]).orElseThrow() : null)
                .paid(eventDto.getPaid())
                .participantLimit(eventDto.getParticipantLimit())
                .requestModeration(eventDto.getRequestModeration() == null || eventDto.getRequestModeration())
                .build();
    }

    @Override
    public EventDto entityToDto(Event event, DtoType... dtoType) {
        if (dtoType.length > 0 && dtoType[0] == DtoType.SHORT) {
            return EventDto.builder()
                    .eventId(event.getEventId())
                    .annotation(event.getAnnotation())
                    .categoryDto(new EventDto.CategoryDto(event.getCategory().getCategoryId(), event.getCategory().getCategoryName()))
                    .confirmedRequests(event.getConfirmedRequests())
                    .eventDate(event.getEventDate())
                    .eventTitle(event.getEventTitle())
                    .initiator(new EventDto.UserShortDto(event.getInitiator().getUserId(), event.getInitiator().getUserName()))
                    .paid(event.isPaid())
                    .views(event.getViews())
                    .build();
        }

        return EventDto.builder()
                .eventId(event.getEventId())
                .annotation(event.getAnnotation())
                .categoryId(event.getCategory().getCategoryId())
                .categoryDto(new EventDto.CategoryDto(event.getCategory().getCategoryId(), event.getCategory().getCategoryName()))
                .confirmedRequests(event.getConfirmedRequests())
                .description(event.getDescription())
                .eventCreated(event.getEventCreated())
                .eventDate(event.getEventDate())
                .eventLocationDto(new EventDto.EventLocationDto(event.getEventLatitude(), event.getEventLongitude()))
                .eventStatus(event.getEventStatus())
                .eventTitle(event.getEventTitle())
                .initiator(new EventDto.UserShortDto(event.getInitiator().getUserId(), event.getInitiator().getUserName()))
                .paid(event.isPaid())
                .participantLimit(event.getParticipantLimit())
                .published(event.getPublished())
                .requestModeration(event.isRequestModeration())
                .views(event.getViews())
                .build();
    }
}
