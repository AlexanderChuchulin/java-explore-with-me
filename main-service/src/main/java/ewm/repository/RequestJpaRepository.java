package ewm.repository;

import ewm.abstraction.EwmJpaRepository;
import ewm.model.Request;
import ewm.other.IdName;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

import static ewm.other.IdName.*;

public interface RequestJpaRepository extends EwmJpaRepository<Request> {

    @Override
    default boolean ewmExistsById(Map<IdName, Long> entityIdMap, boolean isAdmin) {
        return entityIdMap.containsKey(INITIATOR_ID) ?
                existsByEventInitiatorUserIdAndEventEventIdAndRequestId(entityIdMap.get(INITIATOR_ID), entityIdMap.get(EVENT_ID), entityIdMap.get(GENERAL_ID))
                : existsByRequesterUserIdAndRequestId(entityIdMap.get(REQUESTER_ID), entityIdMap.get(GENERAL_ID));
    }

    @Override
    default List<Request> ewmFindAll(Long userId, List<Long> eventId, Pageable pageable, Boolean... booleanParam) {
        return eventId != null ? findAllByEventInitiatorUserIdAndEventEventId(userId, eventId.get(0), pageable)
                : findAllByRequesterUserId(userId, pageable);
    }

    List<Request> findAllByRequesterUserId(Long ownerId, Pageable pageable);

    List<Request> findAllByEventInitiatorUserIdAndEventEventId(Long initiatorId, Long eventId, Pageable pageable);

    boolean existsByRequesterUserIdAndRequestId(Long requesterId, Long requestId);

    boolean existsByEventInitiatorUserIdAndEventEventIdAndRequestId(Long initiatorId, Long eventId, Long requestId);

    boolean existsByRequesterUserIdAndEventEventId(Long ownerId, Long eventId);
}
