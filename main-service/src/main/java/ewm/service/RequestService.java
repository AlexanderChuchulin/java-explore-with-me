package ewm.service;

import ewm.abstraction.EwmAbstractService;
import ewm.dto.RequestDto;
import ewm.exception.EntityNotFoundExc;
import ewm.exception.ValidationExc;
import ewm.mapper.RequestMapper;
import ewm.model.Event;
import ewm.model.Request;
import ewm.other.IdName;
import ewm.other.EventStatus;
import ewm.other.RequestStatus;
import ewm.repository.EventJpaRepository;
import ewm.repository.RequestJpaRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

import static ewm.other.IdName.*;
import static ewm.other.RequestStatus.CANCELED;
import static ewm.other.RequestStatus.CONFIRMED;

@Service
@Slf4j
public class RequestService extends EwmAbstractService<RequestDto, Request> {
    private final EventJpaRepository eventJpaRepository;
    private final RequestJpaRepository requestJpaRepository;

    @Autowired
    public RequestService(EventJpaRepository eventJpaRepository, RequestJpaRepository requestJpaRepository, RequestMapper requestMapper) {
        this.eventJpaRepository = eventJpaRepository;
        this.requestJpaRepository = requestJpaRepository;
        name = "Request";
        mapper = requestMapper;
        jpaRepository = requestJpaRepository;
    }

    public RequestDto changeRequestStatusService(Map<IdName, Long> entityIdMap, RequestStatus changeStatus) {
        String userType = changeStatus == CANCELED ? "Requester" : "Event initiator";
        String action = String.format("%s %s by ID %s", changeStatus.name(), name, entityIdMap.get(GENERAL_ID));

        entityExistCheckService(entityIdMap, false, action);

        Request updatingRequest = jpaRepository.getReferenceById(entityIdMap.get(GENERAL_ID));

        if (changeStatus == CONFIRMED && (updatingRequest.getRequestStatus() == CONFIRMED
                || updatingRequest.getEvent().getParticipantLimit() == 0 || !updatingRequest.getEvent().isRequestModeration())) {
            throw new ValidationExc("Request confirm canceled", "Request already confirmed or confirmation is not required");
        }

        updatingRequest.setRequestStatus(changeStatus);
        log.info(userType + " " + action);
        return mapper.entityToDto(jpaRepository.save(updatingRequest));
    }

    @Override
    public void validateEntityService(RequestDto requestDto, boolean isUpdate, boolean isAdmin, String conclusion, Long... params) {
        StringBuilder excReason = new StringBuilder();
        Event event;

        if (!eventJpaRepository.existsById(params[1])) {
            throw new EntityNotFoundExc("Request create aborted.", "Search event error.");
        } else {
            event = eventJpaRepository.getReferenceById(params[1]);
        }
        if (requestJpaRepository.existsByRequesterUserIdAndEventEventId(params[0], params[1])) {
            excReason.append("Request from this requester for this event already exists");
        }
        if (params[0].longValue() == event.getInitiator().getUserId()) {
            excReason.append("Requester ID must not be same as event initiator ID");
        }
        if (event.getEventStatus() != EventStatus.PUBLISHED) {
            excReason.append("Event status must be published");
        }
        if (event.getParticipantLimit() != 0 && event.getConfirmedRequests() == event.getParticipantLimit()) {
            excReason.append("All seats for this event are full.");
        }

        if (excReason.length() > 0) {
            log.warn("{} validation error. {}{}", name, excReason, conclusion);
            throw new ValidationExc(conclusion, excReason.toString());
        }
    }
}
