package ewm.abstraction;

import ewm.exception.EntityNotFoundExc;
import ewm.exception.ValidationExc;
import ewm.other.DtoType;
import ewm.other.IdName;
import ewm.other.OtherUtils;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static ewm.other.IdName.GENERAL_ID;

@Slf4j
public abstract class EwmAbstractService<T extends EntityDto, V extends EwmEntity> implements EwmService<T> {
    protected String name;
    public EwmJpaRepository<V> jpaRepository;
    protected EntityMapper<T, V> mapper;

    @Override
    public T createEntity(T dto, boolean isHaveDto, Long... params) {
        String conclusion = String.format("%s not created.", name);

        if (isHaveDto) {
            checkDto(dto, conclusion);
        }

        validateEntity(dto, false, false, conclusion, params);
        log.info("Create {}", name);
        return mapper.entityToDto(jpaRepository.save(mapper.dtoToEntity(dto, false, params)));
    }

    @Override
    public T getEntityById(Map<IdName, Long> entityIdMap, DtoType dtoType, boolean isAdmin) {
        String action = String.format("%s get %s by ID %s", isAdmin ? "Admin" : "User", name, entityIdMap.get(GENERAL_ID));

        entityExistCheck(entityIdMap, isAdmin, action);
        log.info(action);
        return mapper.entityToDto(jpaRepository.getReferenceById(entityIdMap.get(GENERAL_ID)));
    }

    @Override
    public List<T> getEntity(Long ownerId, List<Long> entityIds, int from, int size, DtoType dtoType, Boolean... booleanParam) {
        if (entityIds == null | (ownerId == null & entityIds == null)) {
            log.info("Get {}s from {} size {}", name, from, size);
        } else {
            log.info("Get {}s with current or other entity ids {}", name, entityIds);
        }
        return jpaRepository.ewmFindAll(ownerId, entityIds, OtherUtils.pageableCreate(from, size), booleanParam).stream()
                .map(entity -> mapper.entityToDto(entity, dtoType))
                .collect(Collectors.toList());
    }

    @Override
    public T updateEntity(Map<IdName, Long> entityIdMap, T dto, boolean isHaveDto, boolean isAdmin) {
        String conclusion = String.format("%s not updated", name);

        if (isHaveDto) {
            checkDto(dto, conclusion);
        }

        if (!entityIdMap.containsKey(GENERAL_ID)) {
            entityIdMap.put(GENERAL_ID, dto.getId());
        }

        String action = String.format("%s update %s by ID %s", isAdmin ? "Admin" : "User", name, entityIdMap.get(GENERAL_ID));

        entityExistCheck(entityIdMap, isAdmin, action);

        V updatingEntity = mapper.dtoToEntity(dto, true);

        BeanUtils.copyProperties(jpaRepository
                .getReferenceById(entityIdMap.get(GENERAL_ID)), updatingEntity, OtherUtils.getNotNullPropertyNames(updatingEntity));

        validateEntity(mapper.entityToDto(updatingEntity), true, true, conclusion);
        log.info(action);
        return mapper.entityToDto(jpaRepository.save(updatingEntity));
    }

    @Override
    public void deleteEntityById(Map<IdName, Long> entityIdMap) {
        String action = String.format("Delete %s by ID %s", name, entityIdMap.get(GENERAL_ID));

        entityExistCheck(entityIdMap, false, action);
        jpaRepository.deleteById(entityIdMap.get(GENERAL_ID));
        log.info(action);
    }

    /**
     * Метод проверяет существование сущности по id
     */
    public void entityExistCheck(Map<IdName, Long> entityIdMap, boolean isAdmin, String action) {
        String message = String.format("%s aborted. ", action);
        String reason = String.format("Search %s error.", name);

        if (!jpaRepository.ewmExistsById(entityIdMap, isAdmin)) {
            log.info(message, reason);
            throw new EntityNotFoundExc(String.format(message),reason);
        }
    }

    /**
     * Метод проверяет Dto на null или являются ли все поля null
     */
    @SneakyThrows
    public void checkDto(T dto, String conclusion) {
        boolean isAllFieldsNull = true;

        if (dto != null) {
            for (Field f : dto.getClass().getDeclaredFields()) {
                f.setAccessible(true);

                if (f.get(dto) != null) {
                    isAllFieldsNull = false;
                    break;
                }
            }
        }
        if (isAllFieldsNull) {
            throw new ValidationExc(conclusion, "Object or all object fields must not be null.");
        }
    }

    /**
     * Метод проводит валидацию входного Dto сущности
     */
    public abstract void validateEntity(T entityDto, boolean isHaveDto, boolean isAdmin, String conclusion, Long... params);
}
