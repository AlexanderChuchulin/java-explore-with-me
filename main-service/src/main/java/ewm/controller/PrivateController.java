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
        return eventService.createEntityService(eventDto, true, userId);
    }

    @GetMapping("/{userId}/events/{eventId}")
    public EventDto getEventById(@PathVariable long userId, @PathVariable long eventId) {
        return eventService.getEntityByIdService(Map.of(INITIATOR_ID, userId, GENERAL_ID, eventId), DtoType.FULL, false);
    }

    @GetMapping("/{userId}/events")
    public List<EventDto> getEvents(@PathVariable long userId,
                                              @RequestParam(value = "from", required = false, defaultValue = "0") int from,
                                              @RequestParam(value = "size", required = false, defaultValue = "10") int size) {
        return eventService.getEntityService(userId, null, from, size, DtoType.SHORT);
    }

    @PatchMapping("/{userId}/events")
    public EventDto updateEvent(@PathVariable long userId, @RequestBody EventDto eventDto) {
        return eventService.updateEntityService(new HashMap<>(Map.of(INITIATOR_ID, userId)), eventDto, true, false);
    }

    @PatchMapping("/{userId}/events/{eventId}")
    public EventDto cancelEvent(@PathVariable long userId, @PathVariable long eventId) {
        return eventService.changeEventStatusService(Map.of(INITIATOR_ID, userId, GENERAL_ID, eventId), EventStatus.CANCELED, false);
    }

    @PostMapping("/{userId}/requests")
    public RequestDto createRequest(@PathVariable long userId, @RequestParam(value = "eventId") long eventId) {
        return requestService.createEntityService(null, false, userId, eventId);
    }

    @GetMapping("/{userId}/requests")
    public List<RequestDto> getRequests(@PathVariable long userId) {
        return requestService.getEntityService(userId, null, 0, 100, DtoType.BASIC);
    }

    @GetMapping("/{userId}/events/{eventId}/requests")
    public List<RequestDto> getRequestsByEventId(@PathVariable long userId, @PathVariable long eventId) {
        return requestService.getEntityService(userId, List.of(eventId), 0, 100, DtoType.BASIC);
    }

    @PatchMapping("/{userId}/requests/{requestId}/cancel")
    public RequestDto cancelRequest(@PathVariable long userId, @PathVariable long requestId) {
        return requestService.changeRequestStatusService(Map.of(REQUESTER_ID, userId, GENERAL_ID, requestId), CANCELED);
    }

    @PatchMapping("/{userId}/events/{eventId}/requests/{reqId}/confirm")
    public RequestDto confirmRequest(@PathVariable long userId, @PathVariable long eventId, @PathVariable long reqId) {
        return requestService.changeRequestStatusService(Map.of(INITIATOR_ID, userId, GENERAL_ID, reqId, EVENT_ID, eventId), CONFIRMED);
    }

    @PatchMapping("/{userId}/events/{eventId}/requests/{reqId}/reject")
    public RequestDto rejectRequest(@PathVariable long userId, @PathVariable long eventId, @PathVariable long reqId) {
        return requestService.changeRequestStatusService(Map.of(INITIATOR_ID, userId, GENERAL_ID, reqId, EVENT_ID, eventId), REJECTED);
    }
}
