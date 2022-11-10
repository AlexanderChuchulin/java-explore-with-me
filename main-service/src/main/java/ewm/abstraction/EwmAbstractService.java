package ewm.abstraction;

import ewm.exception.EntityNotFoundExc;
import ewm.other.OtherUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public abstract class EwmAbstractService<T extends EntityDto, V extends EwmEntity> implements EwmService<T> {
    protected String name;
    protected JpaRepository<V, Long> jpaRepository;
    protected EntityMapper<T, V> mapper;


    @Override
    public T createEntityService(T dto) {
        validateEntityService(mapper.dtoToEntity(dto), false, String.format("%s not created.", name));
        log.info("{} created {}", name, dto);
        return mapper.entityToDto(jpaRepository.save(mapper.dtoToEntity(dto)));
    }

    @Override
    public T getEntityByIdService(long entityId) {
        entityExistCheckService(entityId, String.format("Get %s by ID %s.", name, entityId));
        log.info("Get {} by ID {}", name, entityId);
        return mapper.entityToDto(jpaRepository.getReferenceById(entityId));
    }

    @Override
    public List<T> getEntityByIdsService(List<Long> entityIds) {
        log.info("Get {} by IDs {}", name, entityIds);
        return jpaRepository.findAllById(entityIds).stream()
                .map(mapper::entityToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<T> getEntityService(int from, int size) {
        log.info("Get {}s from {} size {}", name, from, size);
        return jpaRepository.findAll(OtherUtils
                .pageableCreate(from, size)).stream()
                .map(mapper::entityToDto)
                .collect(Collectors.toList());
    }

    @Override
    public T updateEntityService(long entityId, T entity) {
        return null;
    }

    @Override
    public void deleteEntityByIdService(long entityId) {
        entityExistCheckService(entityId, String.format("Delete %s by ID %s.", name, entityId));
        jpaRepository.deleteById(entityId);
        log.info("Delete {} by ID {}", name, entityId);
    }

    /**
     * Метод проверяет существование сущности по id
     */
    public void entityExistCheckService(Long entityId, String action) {
        if (!jpaRepository.existsById(entityId)) {
            throw new EntityNotFoundExc(String.format("Search entity error. %s aborted", action));
        }
    }

    /**
     * Метод проводит валидацию сущности
     */
    public abstract void validateEntityService(V entity, Boolean isUpdate, String conclusion);


}
