package ewm.abstraction;

import ewm.other.DtoType;

public abstract class EntityMapper<T extends EntityDto, V extends EwmEntity> {

    /**
     Метод преобразования DTO в сущность по схеме New
     */
    public abstract V dtoToEntity(T dto, Long... params);

    /**
     Метод преобразования сущности в DTO по обычной схеме
     */
    public abstract T entityToDto(V entity, DtoType... dtoType);

}
