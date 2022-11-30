package ewm.mapper;

import ewm.abstraction.EntityMapper;
import ewm.dto.CompilationDto;
import ewm.model.Compilation;
import ewm.other.DtoType;
import ewm.repository.EventJpaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
public class CompilationMapper extends EntityMapper<CompilationDto, Compilation> {
    private final EventJpaRepository eventJpaRepository;
    private final EventMapper eventMapper;

    @Autowired
    public CompilationMapper(EventJpaRepository eventJpaRepository, EventMapper eventMapper) {
        this.eventJpaRepository = eventJpaRepository;
        this.eventMapper = eventMapper;
    }

    @Override
    public Compilation dtoToEntity(CompilationDto compilationDto, boolean isUpdate, Long... params) {
        return Compilation.builder()
                .compilationId(compilationDto.getCompilationId())
                .eventsList(eventJpaRepository.findAllById(compilationDto.getEventIds()))
                .pinned(compilationDto.getPinned())
                .compilationTitle(compilationDto.getCompilationTitle())
                .build();
    }

    @Override
    public CompilationDto entityToDto(Compilation compilation, DtoType... dtoType) {
        return CompilationDto.builder()
                .compilationId(compilation.getCompilationId())
                .eventDtoList(compilation.getEventsList().stream()
                        .map(event -> eventMapper.entityToDto(event, DtoType.SHORT)).collect(Collectors.toList()))
                .pinned(compilation.isPinned())
                .compilationTitle(compilation.getCompilationTitle())
                .build();
    }
}
