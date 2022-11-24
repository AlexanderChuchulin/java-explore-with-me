package ewm.abstraction;

import ewm.other.IdName;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.List;
import java.util.Map;

import static ewm.other.IdName.GENERAL_ID;

@NoRepositoryBean
public interface EwmJpaRepository<V extends EwmEntity> extends JpaRepository<V, Long> {

    default List<V> ewmFindAll(Long userId, List<Long> entityIds, Pageable pageable, Boolean... booleanParam) {
        return entityIds != null && entityIds.size() > 0 ? findAllById(entityIds) : findAllList(pageable);
    }

    default List<V> findAllList(Pageable pageable) {
        Page<V> contentPage = findAll(pageable);
        return contentPage.getContent();
    }

    default boolean ewmExistsById(Map<IdName, Long> entityIdMap, boolean booleanParam) {
        return existsById(entityIdMap.get(GENERAL_ID));
    }
}
