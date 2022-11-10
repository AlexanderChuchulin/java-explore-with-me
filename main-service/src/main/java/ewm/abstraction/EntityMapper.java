package ewm.abstraction;

public abstract class EntityMapper<T extends EntityDto, V extends EwmEntity> {

    /**
     Метод преобразования DTO в сущность по схеме New
     */
    public abstract V dtoToEntity(T dto);

    /**
     Метод преобразования сущности в DTO по обычной схеме
     */
    public abstract T entityToDto(V entity);

}
