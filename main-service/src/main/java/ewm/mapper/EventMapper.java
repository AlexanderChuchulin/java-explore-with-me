package ewm.mapper;

import ewm.abstraction.EntityMapper;
import ewm.dto.EventDto;
import ewm.exception.EntityNotFoundExc;
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
    public Event dtoToEntity(EventDto eventDto, boolean isUpdate, Long... params) {
        return Event.builder()
                .eventId(eventDto.getEventId())
                .annotation(eventDto.getAnnotation())
                .category(eventDto.getCategoryId() == null ? null : categoryJpaRepository.findById(eventDto.getCategoryId())
                        .orElseThrow(() -> new EntityNotFoundExc("Dto to event aborted", "Category not found")))
                .confirmedRequests(isUpdate ? eventDto.getConfirmedRequests() : 0L)
                .description(eventDto.getDescription())
                .eventCreated(!isUpdate ? LocalDateTime.now() : eventDto.getEventCreated() != null ? eventDto.getEventCreated() : null)
                .eventDate(isUpdate && eventDto.getEventDateStart() == null ? null : eventDto.getEventDateStart())
                .eventDateEnd(eventDto.getEventDateEnd() != null ? eventDto.getEventDateEnd() : eventDto.getEventDateStart().plusDays(1L))
                .eventLatitude(eventDto.getEventLocationDto() != null ? eventDto.getEventLocationDto().getLat() : null)
                .eventLongitude(eventDto.getEventLocationDto() != null ? eventDto.getEventLocationDto().getLon() : null)
                .eventStatus(!isUpdate ? EventStatus.PENDING : eventDto.getEventStatus())
                .eventTitle(eventDto.getEventTitle())
                .initiator(!isUpdate ? userJpaRepository.findById(params[0]).orElseThrow(()
                        -> new EntityNotFoundExc("Dto to event aborted", "Initiator not found"))
                        : eventDto.getInitiator() == null ? null :
                        userJpaRepository.findById(eventDto.getInitiator().getId()).orElseThrow(()
                                -> new EntityNotFoundExc("Dto to event aborted", "Initiator not found")))
                .paid(isUpdate ? eventDto.getPaid() : eventDto.getPaid() != null ? eventDto.getPaid() : false)
                .participantLimit(isUpdate ? eventDto.getParticipantLimit() : eventDto.getParticipantLimit() != null
                        ? eventDto.getParticipantLimit() : 0)
                .published(isUpdate ? eventDto.getPublished() : null)
                .requestModeration(isUpdate ? eventDto.getRequestModeration() : eventDto.getRequestModeration() != null
                        ? eventDto.getRequestModeration() : true)
                .views(isUpdate ? eventDto.getViews() : 0L)
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
                    .eventDateStart(event.getEventDate())
                    .eventDateEnd(event.getEventDateEnd())
                    .eventTitle(event.getEventTitle())
                    .initiator(new EventDto.UserShortDto(event.getInitiator().getUserId(), event.getInitiator().getUserName(),
                            event.getInitiator().getInitiatorRating() != null ? String.format("%.2f", event.getInitiator().getInitiatorRating()) : null))
                    .paid(event.getPaid())
                    .views(event.getViews())
                    .eventRating(event.getEventRating() != null ? String.format("%.1f", event.getEventRating()) : null)
                    .partOfVoted(event.getPartOfVoted() != null ? String.format("%.1f%%", event.getPartOfVoted() * 100) : null)
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
                .eventDateStart(event.getEventDate())
                .eventDateEnd(event.getEventDateEnd())
                .eventLocationDto(new EventDto.EventLocationDto(event.getEventLatitude(), event.getEventLongitude()))
                .eventStatus(event.getEventStatus())
                .eventTitle(event.getEventTitle())
                .initiator(new EventDto.UserShortDto(event.getInitiator().getUserId(), event.getInitiator().getUserName(),
                        event.getInitiator().getInitiatorRating() != null ? String.format("%.2f", event.getInitiator().getInitiatorRating()) : null))
                .paid(event.getPaid())
                .participantLimit(event.getParticipantLimit())
                .published(event.getPublished())
                .requestModeration(event.getRequestModeration())
                .views(event.getViews())
                .eventRating(event.getEventRating() != null ? String.format("%.1f", event.getEventRating()) : null)
                .partOfVoted(event.getPartOfVoted() != null ? String.format("%.1f%%", event.getPartOfVoted() * 100) : null)
                .build();
    }
}
