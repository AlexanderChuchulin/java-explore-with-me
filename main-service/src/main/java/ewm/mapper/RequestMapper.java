package ewm.mapper;

import ewm.abstraction.EntityMapper;
import ewm.dto.RequestDto;
import ewm.exception.EntityNotFoundExc;
import ewm.model.Event;
import ewm.model.Request;
import ewm.other.DtoType;
import ewm.other.RequestStatus;
import ewm.repository.EventJpaRepository;
import ewm.repository.UserJpaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class RequestMapper extends EntityMapper<RequestDto, Request> {
    private final EventJpaRepository eventJpaRepository;
    private final UserJpaRepository userJpaRepository;

    @Autowired
    public RequestMapper(EventJpaRepository eventJpaRepository, UserJpaRepository userJpaRepository) {
        this.eventJpaRepository = eventJpaRepository;
        this.userJpaRepository = userJpaRepository;
    }

    @Override
    public Request dtoToEntity(RequestDto dto, boolean isUpdate, Long... params) {
        Event event = eventJpaRepository.findById(params[1]).orElseThrow();

        return Request.builder()
                .requestCreated(LocalDateTime.now())
                .event(eventJpaRepository.findById(params[1])
                        .orElseThrow(() -> new EntityNotFoundExc("Dto to request aborted", "Event not found")))
                .requester(userJpaRepository.findById(params[0])
                        .orElseThrow(() -> new EntityNotFoundExc("Dto to request aborted", "Requester not found")))
                .requestStatus(event.isRequestModeration() ? RequestStatus.PENDING : RequestStatus.CONFIRMED)
                .build();
    }

    @Override
    public RequestDto entityToDto(Request request, DtoType... dtoType) {
        return RequestDto.builder()
                .requestId(request.getRequestId())
                .requestCreated(request.getRequestCreated())
                .eventId(request.getEvent().getEventId())
                .requesterId(request.getRequester().getUserId())
                .requestStatus(request.getRequestStatus())
                .ratingFromRequester(request.getRatingFromRequester())
                .build();
    }
}
