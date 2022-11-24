package ewm.service;

import ewm.abstraction.EwmAbstractService;
import ewm.dto.CompilationDto;
import ewm.exception.EntityNotFoundExc;
import ewm.exception.ValidationExc;
import ewm.mapper.CompilationMapper;
import ewm.model.Compilation;
import ewm.repository.CompilationJpaRepository;
import ewm.repository.EventJpaRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static ewm.other.IdName.EVENT_ID;
import static ewm.other.IdName.GENERAL_ID;

@Service
@Slf4j
public class CompilationService extends EwmAbstractService<CompilationDto, Compilation> {
    private final CompilationJpaRepository compilationJpaRepository;
    private final EventJpaRepository eventJpaRepository;

    @Autowired
    public CompilationService(CompilationJpaRepository compilationJpaRepository,
                              CompilationMapper compilationMapper, EventJpaRepository eventJpaRepository) {
        this.compilationJpaRepository = compilationJpaRepository;
        this.eventJpaRepository = eventJpaRepository;
        name = "Compilation";
        jpaRepository = compilationJpaRepository;
        mapper = compilationMapper;
    }

    public void compilationPinUpdate(long compilationId, boolean isPin) {
        String action = String.format("Admin %s %s by ID %s", isPin ? "pin" : "unpin", name, compilationId);

        entityExistCheckService(Map.of(GENERAL_ID, compilationId), true, action);
        Compilation updatingCompilation = compilationJpaRepository.getReferenceById(compilationId);

        updatingCompilation.setPinned(isPin);
        log.info(action);
        jpaRepository.save(updatingCompilation);
    }

    public void compilationEventUpdate(long compilationId, long eventId, boolean isAdd) {
        String action = String.format("Admin %s event with id %s to %s by ID %s", isAdd ? "add" : "delete", eventId, name, compilationId);

        entityExistCheckService(Map.of(GENERAL_ID, compilationId), true, action);
        Compilation updatingCompilation = compilationJpaRepository.getReferenceById(compilationId);

        if (isAdd) {
            if (!eventJpaRepository.existsByEventIdAndPublishedNotNull(eventId)) {
                throw new EntityNotFoundExc(action + "aborted", "Event not exist or not published");
            }
            updatingCompilation.getEventsList().add(eventJpaRepository.getReferenceById(eventId));
        } else {
            entityExistCheckService(Map.of(GENERAL_ID, compilationId, EVENT_ID, eventId), true, action);
            updatingCompilation.getEventsList().remove(eventJpaRepository.getReferenceById(eventId));
        }
        log.info(action);
        jpaRepository.save(updatingCompilation);
    }

    @Override
    public void validateEntityService(CompilationDto compilationDto, boolean isUpdate, boolean isAdmin, String conclusion, Long... params) {
        StringBuilder excReason = new StringBuilder();
        List<Long> wrongEventIds = new ArrayList<>();

        for (Long eventId : compilationDto.getEventIds()) {
            if (!eventJpaRepository.existsById(eventId)) {
                wrongEventIds.add(eventId);
            }
        }

        if (wrongEventIds.size() > 0) {
            excReason.append(String.format("Not Exist Events with id %s", wrongEventIds));
        }
        if (compilationDto.getCompilationTitle() == null || compilationDto.getCompilationTitle().length() < 5
                || compilationDto.getCompilationTitle().length() > 100) {
            excReason.append("Compilation title length must be more than 5 and less than 100 symbols. ");
        }
        if (compilationDto.getPinned() == null) {
            excReason.append("Compilation pinned status must be specified ");
        }

        if (excReason.length() > 0) {
            log.warn("{} validation error. {}{}", name, excReason, conclusion);
            throw new ValidationExc(conclusion, excReason.toString());
        }
    }
}
