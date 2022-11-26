package ewm.repository;

import ewm.abstraction.EwmJpaRepository;
import ewm.model.Event;
import ewm.other.IdName;
import ewm.other.EventStatus;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static ewm.other.IdName.GENERAL_ID;
import static ewm.other.IdName.INITIATOR_ID;

@Repository
public interface EventJpaRepository extends EwmJpaRepository<Event> {
    @Override
    default boolean ewmExistsById(Map<IdName, Long> entityIdMap, boolean isAdmin) {
        if (isAdmin) {
            return existsById(entityIdMap.get(GENERAL_ID));
        } else if (entityIdMap.containsKey(INITIATOR_ID)) {
            return existsByInitiatorUserIdAndEventId(entityIdMap.get(INITIATOR_ID), entityIdMap.get(GENERAL_ID));
        }
        return existsByEventIdAndPublishedNotNull(entityIdMap.get(GENERAL_ID));
    }

    @Override
    default List<Event> ewmFindAll(Long ownerId, List<Long> entityIds, Pageable pageable, Boolean... booleanParam) {
        return findAllByInitiatorUserId(ownerId, pageable);
    }

    boolean existsByInitiatorUserIdAndEventId(Long ownerId, Long eventId);

    boolean existsByEventIdAndPublishedNotNull(Long eventId);

    List<Event> findAllByInitiatorUserId(Long ownerId, Pageable pageable);

    @Query("select event from Event event where (:usersIds is null or event.initiator.userId in :usersIds) " +
            "and (:eventStatuses is null or event.eventStatus in :eventStatuses) " +
            "and (:categoryIds is null or event.category.categoryId in :categoryIds) " +
            "and (event.eventDate > :rangeStart and event.eventDate < :rangeEnd)")
    List<Event> findAllEventsForAdmin(List<Long> usersIds, List<EventStatus> eventStatuses, List<Long> categoryIds,
                                      LocalDateTime rangeStart, LocalDateTime rangeEnd, Pageable pageable);

    @Query("select event from Event event where (event.eventStatus = 'PUBLISHED') " +
            "and (:searchText is null or (lower(event.annotation) like lower(concat('%', :searchText, '%')) " +
            "or (event.description) like lower(concat('%', :searchText, '%'))))" +
            "and (:categoryIds is null or event.category.categoryId in :categoryIds) " +
            "and (:isPaid is null or event.paid = :isPaid ) " +
            "and (event.eventDate > :rangeStart and event.eventDate < :rangeEnd)" +
            "and (:isOnlyAvailable is false or (event.participantLimit = 0 or event.confirmedRequests < event.participantLimit))")
    List<Event> findAllEventsForPublic(String searchText, List<Long> categoryIds, Boolean isPaid, LocalDateTime rangeStart,
                                       LocalDateTime rangeEnd, Boolean isOnlyAvailable, Pageable pageable);

    @Transactional
    @Modifying
    @Query("update Event event set event.views = :hits where event.eventId = :eventId")
    void setViewsForEvent(Long eventId, Long hits);
}