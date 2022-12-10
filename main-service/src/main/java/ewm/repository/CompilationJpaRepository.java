package ewm.repository;

import ewm.abstraction.EwmJpaRepository;
import ewm.model.Compilation;
import ewm.other.IdName;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

import static ewm.other.IdName.*;

@Repository
public interface CompilationJpaRepository extends EwmJpaRepository<Compilation> {
    @Override
    default boolean ewmExistsById(Map<IdName, Long> entityIdMap, boolean isPined) {
        if (entityIdMap.containsKey(EVENT_ID)) {
            return existsByCompilationIdAndEventId(entityIdMap.get(GENERAL_ID), entityIdMap.get(EVENT_ID));
        } else {
            return existsById(entityIdMap.get(GENERAL_ID));
        }
    }

    @Override
    default List<Compilation> ewmFindAll(Long userId, List<Long> entityIds, Pageable pageable, Boolean... booleanParam) {
        return findAllWithPinned(booleanParam[0], pageable);
    }

    @EntityGraph(attributePaths = {"eventsList", "eventsList.category", "eventsList.initiator"})
    @Query("select compilation from Compilation compilation where (:isPinned is null or compilation.pinned in :isPinned)")
    List<Compilation> findAllWithPinned(Boolean isPinned, Pageable pageable);

    @Query("select case when count(compilation) > 0 then true else false end from Compilation compilation join " +
            "compilation.eventsList as events where compilation.compilationId = :compilationId and :eventId in events.eventId")
    boolean existsByCompilationIdAndEventId(long compilationId, long eventId);
}
