package ewm.service;

import ewm.abstraction.EwmAbstractService;
import ewm.dto.EventDto;
import ewm.exception.ValidationExc;
import ewm.mapper.EventMapper;
import ewm.model.Event;
import ewm.other.*;
import ewm.repository.EventJpaRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static ewm.other.IdName.*;
import static ewm.other.EventStatus.*;

@Service
@Slf4j
public class EventService extends EwmAbstractService<EventDto, Event> {
    private final EventJpaRepository eventJpaRepository;

    @Autowired
    public EventService(EventJpaRepository eventJpaRepository, EventMapper eventMapper) {
        name = "Event";
        mapper = eventMapper;
        this.eventJpaRepository = eventJpaRepository;
        jpaRepository = eventJpaRepository;
    }

    public List<EventDto> getEventsForAdminService(List<Long> userIds, List<EventStatus> eventStatuses, List<Long> categoryIds,
                                                   String rangeStart, String rangeEnd, int from, int size) {

        log.info("Get events for admin with userIds {}, eventStatuses {}, categoryIds {}, rangeStart {}, rangeEnd {}, from {}, size {}",
                userIds, eventStatuses, categoryIds, rangeStart, rangeEnd, from, size);

        return eventJpaRepository.findAllEventsForAdmin(userIds, eventStatuses, categoryIds, getCorrectDateTime(rangeStart),
                        getCorrectDateTime(rangeEnd), OtherUtils.pageableCreate(from, size)).stream()
                .map(entity -> mapper.entityToDto(entity, DtoType.FULL))
                .collect(Collectors.toList());
    }

    public List<EventDto> getEventsForPublicService(String searchText, List<Long> categoryIds, Boolean isPaid, String rangeStart,
                                                    String rangeEnd, Boolean isOnlyAvailable, int from, int size, String sortParam) {
        if (rangeStart == null || rangeStart.isBlank()) {
            rangeStart = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        }

        log.info("Get events for public with searchText {}, categoryIds {}, isPaid {}, rangeStart {}, rangeEnd {}, " +
                        "isOnlyAvailable {}, from {}, size {}, sort {}", searchText, categoryIds, isPaid, rangeStart, rangeEnd,
                isOnlyAvailable, from, size, sortParam);

        return eventJpaRepository.findAllEventsForPublic(searchText, categoryIds, isPaid, getCorrectDateTime(rangeStart),
                        getCorrectDateTime(rangeEnd), isOnlyAvailable, OtherUtils.pageableCreate(from, size, sortParam)).stream()
                .map(entity -> mapper.entityToDto(entity, DtoType.SHORT))
                .collect(Collectors.toList());
    }

    public EventDto changeEventStatusService(Map<IdName, Long> entityIdMap, EventStatus changeStatus, boolean isAdmin) {
        String userType = isAdmin ? "Admin" : "Initiator";
        String action = String.format("%s %s by ID %s", changeStatus.name(), name, entityIdMap.get(GENERAL_ID));

        entityExistCheckService(entityIdMap, isAdmin, userType + action);

        Event updatingEvent = jpaRepository.getReferenceById(entityIdMap.get(GENERAL_ID));

        if (changeStatus == CANCELED && updatingEvent.getEventStatus() == PUBLISHED) {
            throw new ValidationExc("Event not canceled by " + userType, "Status should not be published");
        }
        if (changeStatus == PUBLISHED && (updatingEvent.getEventStatus() != PENDING
                || !updatingEvent.getEventDate().isAfter(LocalDateTime.now().plusHours(1L)))) {
            throw new ValidationExc("Event not published by admin",
                    "Status should be pending and event date must be on 1 hour later than publication date");
        }

        if (changeStatus == PUBLISHED) {
            updatingEvent.setPublished(LocalDateTime.now());
        }

        updatingEvent.setEventStatus(changeStatus);
        log.info(userType + " " + action);
        return mapper.entityToDto(jpaRepository.save(updatingEvent));
    }


    /**
     * Метод проверяет строку с датой на соответствие паттернам и возвращает LocalDateTime или бросает исключение
     */
    public LocalDateTime getCorrectDateTime(String textDateTime) {
        LocalDate parseDate = null;
        LocalTime parseTime = LocalTime.of(0, 0, 0);

        if (textDateTime.length() == 19) {
            try {
                return LocalDateTime.parse(textDateTime, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            } catch (Exception ignore) {
            }
        }

        if (textDateTime.length() == 10) {
            try {
                parseDate = LocalDate.parse(textDateTime, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            } catch (Exception ignore) {
            }
        }

        if (textDateTime.length() > 10 & textDateTime.length() <= 19) {
            try {
                parseDate = LocalDate.parse(textDateTime.split(" ")[0], DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            } catch (Exception ignore) {
            }

            try {
                parseTime = LocalTime.parse(textDateTime.split(" ")[1], DateTimeFormatter.ofPattern("HH:mm:ss"));
            } catch (Exception ignore) {
            }
        }

        if (parseDate != null) {
            return LocalDateTime.of(parseDate, parseTime);
        }
        throw new ValidationExc("Operation aborted", "Bad date and time format");
    }

    @Override
    public void validateEntityService(EventDto eventDto, boolean isUpdate, boolean isAdmin, String conclusion, Long... params) {
        StringBuilder excReason = new StringBuilder();

        if (eventDto.getAnnotation() == null || eventDto.getAnnotation().length() < 20 || eventDto.getAnnotation().length() > 2000) {
            excReason.append("Annotation length must be more than 20 and less than 2000 symbols. ");
        }
        if (eventDto.getCategoryId() == null) {
            excReason.append("Event category must be specified. ");
        }
        if (eventDto.getDescription() == null || eventDto.getDescription().length() < 20 || eventDto.getDescription().length() > 7000) {
            excReason.append("Description length must be more than 20 and less than 7000 symbols. ");
        }
        if (eventDto.getEventDate().isBefore(eventDto.getEventCreated().plusHours(2L))) {
            excReason.append("Event date must be 2 hours later than the creation date. ");
        }
        if (eventDto.getEventLocationDto() == null || eventDto.getEventLocationDto().getLat() == null
                || eventDto.getEventLocationDto().getLon() == null) {
            excReason.append("Event location latitude and longitude must be specified. ");
        }
        if (eventDto.getEventTitle() == null || eventDto.getEventTitle().length() < 3 || eventDto.getEventTitle().length() > 120) {
            excReason.append("Title length must be more than 3 and less than 120 symbols. ");
        }
        if (isUpdate && !isAdmin && jpaRepository.getReferenceById(eventDto.getEventId()).getEventStatus() == PUBLISHED) {
            excReason.append("Update is not possible for an event with published status. ");
        }
        if (isUpdate && isAdmin) {
             eventDto.setEventStatus(jpaRepository.getReferenceById(eventDto.getEventId()).getEventStatus());
        }

        if (excReason.length() > 0) {
            log.warn("{} validation error. {}{}", name, excReason, conclusion);
            throw new ValidationExc(conclusion, excReason.toString());
        }
    }
}
