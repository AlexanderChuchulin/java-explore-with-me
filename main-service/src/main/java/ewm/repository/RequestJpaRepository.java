package ewm.repository;

import ewm.abstraction.EwmJpaRepository;
import ewm.model.Request;
import ewm.other.IdName;
import ewm.other.RequestStatus;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Map;

import static ewm.other.IdName.*;

public interface RequestJpaRepository extends EwmJpaRepository<Request> {

    @Override
    default boolean ewmExistsById(Map<IdName, Long> entityIdMap, boolean isAdmin) {
        return entityIdMap.containsKey(INITIATOR_ID) ?
                existsByEventInitiatorUserIdAndEventEventIdAndRequestId(entityIdMap.get(INITIATOR_ID),
                        entityIdMap.get(EVENT_ID), entityIdMap.get(GENERAL_ID))
                : existsByRequesterUserIdAndRequestId(entityIdMap.get(REQUESTER_ID), entityIdMap.get(GENERAL_ID));
    }

    @Override
    default List<Request> ewmFindAll(Long userId, List<Long> eventId, Pageable pageable, Boolean... booleanParam) {
        return eventId != null ? findAllByEventInitiatorUserIdAndEventEventId(userId, eventId.get(0), pageable)
                : findAllByRequesterUserId(userId, pageable);
    }

    @EntityGraph(attributePaths = {"event"})
    List<Request> findAllByRequesterUserId(Long ownerId, Pageable pageable);

    @EntityGraph(attributePaths = {"event"})
    List<Request> findAllByEventInitiatorUserIdAndEventEventId(Long initiatorId, Long eventId, Pageable pageable);

    boolean existsByRequesterUserIdAndRequestId(Long requesterId, Long requestId);

    boolean existsByEventInitiatorUserIdAndEventEventIdAndRequestId(Long initiatorId, Long eventId, Long requestId);

    boolean existsByRequesterUserIdAndEventEventIdAndRequestStatusIsNot(Long ownerId, Long eventId, RequestStatus requestStatus);

    @Query("select avg(requests.ratingFromRequester) FROM Request requests " +
            "WHERE requests.event.eventId = :eventId and requests.requestStatus = 'CONFIRMED'")
    double getAvgRatingFromRequesters(long eventId);

    @Query(value = "select cast(1 AS float) * count(case when REQUESTS.RATING_FROM_REQUESTER is not null THEN 1 END)/count(*)" +
            "from REQUESTS where EVENT_ID = :eventId and REQUEST_STATUS = 'CONFIRMED'", nativeQuery = true)
    double getPartOfVotedRequesters(long eventId);
}
