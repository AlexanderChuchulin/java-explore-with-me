package ewm.controller;

import ewm.dto.EventDto;
import ewm.dto.RequestDto;
import ewm.other.*;
import ewm.service.EventService;
import ewm.service.RequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static ewm.other.IdName.*;
import static ewm.other.RequestStatus.*;


@RestController
@RequestMapping("/users")
public class PrivateController {
    private final EventService eventService;
    private final RequestService requestService;

    @Autowired
    public PrivateController(EventService eventService, RequestService requestService) {
        this.eventService = eventService;
        this.requestService = requestService;
    }

    @PostMapping("/{userId}/events")
    public EventDto createEvent(@PathVariable long userId, @RequestBody(required = false) EventDto eventDto) {
        return eventService.createEntity(eventDto, true, userId);
    }

    @GetMapping("/{userId}/events/{eventId}")
    public EventDto getEventById(@PathVariable long userId, @PathVariable long eventId) {
        return eventService.getEntityById(Map.of(INITIATOR_ID, userId, GENERAL_ID, eventId), DtoType.FULL, false);
    }

    @GetMapping("/{userId}/events")
    public List<EventDto> getEvents(@PathVariable long userId,
                                    @RequestParam(value = "from", required = false, defaultValue = "0") int from,
                                    @RequestParam(value = "size", required = false, defaultValue = "10") int size) {
        return eventService.getEntity(userId, null, from, size, DtoType.SHORT);
    }

    @PatchMapping("/{userId}/events")
    public EventDto updateEvent(@PathVariable long userId, @RequestBody EventDto eventDto) {
        return eventService.updateEntity(new HashMap<>(Map.of(INITIATOR_ID, userId)), eventDto, true, false);
    }

    @PatchMapping("/{userId}/events/{eventId}")
    public EventDto cancelEvent(@PathVariable long userId, @PathVariable long eventId) {
        return eventService.changeEventStatus(Map.of(INITIATOR_ID, userId, GENERAL_ID, eventId), EventStatus.CANCELED, false);
    }

    @PostMapping("/{userId}/requests")
    public RequestDto createRequest(@PathVariable long userId, @RequestParam(value = "eventId") long eventId) {
        return requestService.createRequest(userId, eventId);
    }

    @GetMapping("/{userId}/requests")
    public List<RequestDto> getRequests(@PathVariable long userId) {
        return requestService.getEntity(userId, null, 0, 100, DtoType.BASIC);
    }

    @GetMapping("/{userId}/events/{eventId}/requests")
    public List<RequestDto> getRequestsByEventId(@PathVariable long userId, @PathVariable long eventId) {
        return requestService.getEntity(userId, List.of(eventId), 0, 100, DtoType.BASIC);
    }

    @PatchMapping("/{userId}/requests/{requestId}/cancel")
    public RequestDto cancelRequest(@PathVariable long userId, @PathVariable long requestId) {
        return requestService.changeRequestStatus(Map.of(REQUESTER_ID, userId, GENERAL_ID, requestId), CANCELED);
    }

    @PatchMapping("/{userId}/events/{eventId}/requests/{reqId}/confirm")
    public RequestDto confirmRequest(@PathVariable long userId, @PathVariable long eventId, @PathVariable long reqId) {
        return requestService.changeRequestStatus(Map.of(INITIATOR_ID, userId, GENERAL_ID, reqId, EVENT_ID, eventId), CONFIRMED);
    }

    @PatchMapping("/{userId}/events/{eventId}/requests/{reqId}/reject")
    public RequestDto rejectRequest(@PathVariable long userId, @PathVariable long eventId, @PathVariable long reqId) {
        return requestService.changeRequestStatus(Map.of(INITIATOR_ID, userId, GENERAL_ID, reqId, EVENT_ID, eventId), REJECTED);
    }

    @PatchMapping("/{userId}/requests/{requestId}/rating/{rating}")
    public RequestDto createEventRating(@PathVariable long userId, @PathVariable long requestId, @PathVariable int rating) {
        return requestService.createRatingFromRequester(Map.of(REQUESTER_ID, userId, GENERAL_ID, requestId), rating);
    }
}
