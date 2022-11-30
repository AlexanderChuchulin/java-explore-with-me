package ewm.service;

import ewm.abstraction.EwmAbstractService;
import ewm.dto.RequestDto;
import ewm.exception.EntityNotFoundExc;
import ewm.exception.ValidationExc;
import ewm.mapper.RequestMapper;
import ewm.model.Event;
import ewm.model.Request;
import ewm.model.User;
import ewm.other.EventStatus;
import ewm.other.IdName;
import ewm.other.RequestStatus;
import ewm.repository.EventJpaRepository;
import ewm.repository.RequestJpaRepository;
import ewm.repository.UserJpaRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static ewm.other.IdName.GENERAL_ID;
import static ewm.other.RequestStatus.*;

@Service
@Slf4j
public class RequestService extends EwmAbstractService<RequestDto, Request> {
    private final EventJpaRepository eventJpaRepository;
    private final RequestJpaRepository requestJpaRepository;
    private final UserJpaRepository userJpaRepository;

    @Autowired
    public RequestService(EventJpaRepository eventJpaRepository, RequestJpaRepository requestJpaRepository,
                          RequestMapper requestMapper, UserJpaRepository userJpaRepository) {
        this.eventJpaRepository = eventJpaRepository;
        this.requestJpaRepository = requestJpaRepository;
        this.userJpaRepository = userJpaRepository;
        name = "Request";
        mapper = requestMapper;
        jpaRepository = requestJpaRepository;
    }

    public RequestDto createRequest(long userId, long eventId) {
        String conclusion = String.format("%s not created.", name);

        if (!userJpaRepository.existsById(userId)) {
            throw new EntityNotFoundExc(String.format(conclusion),"Requester not found");
        }

        validateEntity(null, false, false, conclusion, userId, eventId);

        if (!eventJpaRepository.getReferenceById(eventId).isRequestModeration()) {
            eventJpaRepository.setConfirmedRequests(eventId, 1L);
        }
        log.info("Create {}", name);
        return mapper.entityToDto(jpaRepository.save(mapper.dtoToEntity(null, false, userId, eventId)));
    }

    public RequestDto changeRequestStatus(Map<IdName, Long> entityIdMap, RequestStatus changeStatus) {
        String userType = changeStatus == CANCELED ? "Requester" : "Event initiator";
        String action = String.format("%s %s by ID %s", changeStatus.name(), name, entityIdMap.get(GENERAL_ID));

        entityExistCheck(entityIdMap, false, action);
        Request updatingRequest = jpaRepository.getReferenceById(entityIdMap.get(GENERAL_ID));

        if (LocalDateTime.now().isAfter(updatingRequest.getEvent().getEventDate())) {
            throw new ValidationExc("Request change status canceled",
                    "Changing the request status after the start of the event is prohibited");
        }
        if (changeStatus == CONFIRMED && (updatingRequest.getRequestStatus() == CONFIRMED
                || updatingRequest.getEvent().getParticipantLimit() == 0)) {
            throw new ValidationExc("Request confirm canceled", "Request already confirmed or confirmation is not required");
        }
        if (changeStatus == REJECTED && (updatingRequest.getRequestStatus() == CONFIRMED)) {
            throw new ValidationExc("Request reject canceled", "Rejecting a request after confirmation is not allowed");
        }

        if (changeStatus == CONFIRMED) {
            eventJpaRepository.setConfirmedRequests(updatingRequest.getEvent().getEventId(), 1L);
        }
        if (updatingRequest.getRequestStatus() == CONFIRMED && changeStatus == CANCELED) {
            eventJpaRepository.setConfirmedRequests(updatingRequest.getEvent().getEventId(), -1L);
        }

        updatingRequest.setRequestStatus(changeStatus);

        log.info(userType + " " + action);
        return mapper.entityToDto(jpaRepository.save(updatingRequest));
    }

    public RequestDto createRatingFromRequester(Map<IdName, Long> entityIdMap, int ratingFromRequester) {
        String action = String.format("Create event rating %s in %s by ID %s", ratingFromRequester, name, entityIdMap.get(GENERAL_ID));
        StringBuilder excReason = new StringBuilder();

        entityExistCheck(entityIdMap, false, action);
        Request updatingRequest = jpaRepository.getReferenceById(entityIdMap.get(GENERAL_ID));

        if (LocalDateTime.now().isBefore(updatingRequest.getEvent().getEventDateEnd())) {
            excReason.append("The rating of an event can only be set after the end date of the event. ");
        }
        if (updatingRequest.getRequestStatus() != CONFIRMED) {
            excReason.append("The status of the request must be confirmed. ");
        }
        if (ratingFromRequester <= 0 || ratingFromRequester > 10) {
            excReason.append("Rating must be set greater than 0 and less than 10. ");
        }
        if (excReason.length() > 0) {
            log.warn("{} validation error. {}{}", name, excReason, action + " aborted");
            throw new ValidationExc(action + " aborted", excReason.toString());
        }

        //requestJpaRepository.setRatingFromRequester(updatingRequest.getEvent().getEventId(), ratingFromRequester);
        updatingRequest.setRatingFromRequester(ratingFromRequester);
        requestJpaRepository.save(updatingRequest);

        log.info(action);
        setEventAndInitiatorRating(updatingRequest.getEvent().getEventId(), updatingRequest.getEvent().getInitiator().getUserId());
        return mapper.entityToDto(jpaRepository.getReferenceById(updatingRequest.getRequestId()));
    }

    public void setEventAndInitiatorRating(long eventId, long initiatorId) {
        Event updatingEvent = eventJpaRepository.getReferenceById(eventId);
        User updatingUser = userJpaRepository.getReferenceById(initiatorId);
        double avgEventRating = requestJpaRepository.getAvgRatingFromRequesters(eventId);
        double partOfVoted = requestJpaRepository.getPartOfVotedRequesters(eventId);
        double initiatorRating;

        updatingEvent.setEventRating(avgEventRating);
        updatingEvent.setPartOfVoted(partOfVoted);
        eventJpaRepository.save(updatingEvent);
        log.info("Set average event rating {} from requests ratings, part of voted {}  for event ID {}", avgEventRating, partOfVoted, eventId);

        List<Event> allEventsByOneInitiator = eventJpaRepository.findAllByInitiatorUserIdAndEventRatingNotNull(initiatorId);

        double sumEventRatingWithWeight = 0;
        double sumEventWeight = 0;

        for (Event event : allEventsByOneInitiator) {
            double eventWeight = event.getPartOfVoted() * event.getConfirmedRequests() + event.getConfirmedRequests();

            sumEventRatingWithWeight += event.getEventRating() * eventWeight;
            sumEventWeight += eventWeight;
        }
        initiatorRating = sumEventRatingWithWeight / sumEventWeight;

        updatingUser.setInitiatorRating(initiatorRating);
        userJpaRepository.save(updatingUser);

        userJpaRepository.getReferenceById(initiatorId).setInitiatorRating(initiatorRating);
        log.info("Calculate rating {} for initiator ID {}", initiatorRating, initiatorId);
    }

    @Override
    public void validateEntity(RequestDto requestDto, boolean isUpdate, boolean isAdmin, String conclusion, Long... params) {
        StringBuilder excReason = new StringBuilder();
        Event event;

        if (!eventJpaRepository.existsById(params[1])) {
            throw new EntityNotFoundExc("Request create aborted.", "Search event error.");
        } else {
            event = eventJpaRepository.getReferenceById(params[1]);
        }
        if (requestJpaRepository.existsByRequesterUserIdAndEventEventIdAndRequestStatusIsNot(params[0], params[1], CANCELED)) {
            excReason.append("Request from this requester for this event already exists. ");
        }
        if (params[0].longValue() == event.getInitiator().getUserId()) {
            excReason.append("Requester ID must not be same as event initiator ID. ");
        }
        if (event.getEventStatus() != EventStatus.PUBLISHED) {
            excReason.append("Event status must be published. ");
        }
        if (event.getParticipantLimit() != 0 && event.getConfirmedRequests() == event.getParticipantLimit()) {
            excReason.append("All seats for this event are full. ");
        }

        if (excReason.length() > 0) {
            log.warn("{} validation error. {}{}", name, excReason, conclusion);
            throw new ValidationExc(conclusion, excReason.toString());
        }
    }
}
