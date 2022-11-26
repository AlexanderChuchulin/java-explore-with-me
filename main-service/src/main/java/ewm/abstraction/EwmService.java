package ewm.abstraction;

import ewm.other.DtoType;
import ewm.other.IdName;

import java.util.List;
import java.util.Map;

public interface EwmService<T extends EntityDto> {
    /**
    Метод создаёт сущность
    */
    T createEntityService(T dto, boolean isHaveDto, Long... params);

    /**
     Метод возвращает одну сущность по заданному ID
     */
    T getEntityByIdService(Map<IdName, Long> entityIdMap, DtoType dtoType, boolean isAdmin);

    /**
     Метод возвращает список сущностей
     */
    List<T> getEntityService(Long ownerId, List<Long> entityIds, int from, int size, DtoType dtoType, Boolean... booleanParam);

    /**
    Метод обновляет сущность по ID
    */
    T updateEntityService(Map<IdName, Long> entityIdMap, T dto, boolean isHaveDto, boolean isAdmin);

    /**
    Метод удаляет все сущности или одну сущность по заданному ID
    */
    void deleteEntityByIdService(Map<IdName, Long> entityIdMap);
}
