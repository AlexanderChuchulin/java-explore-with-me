package ewm.abstraction;

import java.util.List;

public interface EwmService<T extends EntityDto> {
    /**
    Метод создаёт сущность
    */
    T createEntityService(T dto);

    /**
     Метод возвращает одну сущность по заданному id
     */
    T getEntityByIdService(long entityId);

    /**
     Метод возвращает список сущностей по заданным id
     */
    List<T> getEntityByIdsService(List<Long> entityIds);

    /**
     Метод возвращает список сущностей
     */
    List<T> getEntityService(int from, int size);

    /**
    Метод обновляет сущность
    */
    T updateEntityService(long entityId, T dto);

    /**
    Метод удаляет все сущности или одну сущность по заданному id
    */
    void deleteEntityByIdService(long entityId);
}
