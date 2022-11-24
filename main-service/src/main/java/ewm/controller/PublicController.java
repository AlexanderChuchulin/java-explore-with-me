package ewm.controller;

import ewm.client.StatClientService;
import ewm.dto.CategoryDto;
import ewm.dto.CompilationDto;
import ewm.dto.EventDto;
import ewm.service.CategoryService;
import ewm.service.CompilationService;
import ewm.service.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

import static ewm.other.DtoType.BASIC;
import static ewm.other.DtoType.FULL;
import static ewm.other.IdName.*;

@RestController
public class PublicController {
    private final CategoryService categoryService;
    private final CompilationService compilationService;
    private final EventService eventService;
    private final StatClientService statClientService;

    @Autowired
    public PublicController(CategoryService categoryService, CompilationService compilationService,
                            EventService eventService, StatClientService statClientService) {
        this.categoryService = categoryService;
        this.compilationService = compilationService;
        this.eventService = eventService;
        this.statClientService = statClientService;
    }

    @GetMapping("/categories/{categoryId}")
    public CategoryDto getCategoryByIdController(@PathVariable long categoryId) {
        return categoryService.getEntityByIdService(Map.of(GENERAL_ID, categoryId), BASIC, true);
    }

    @GetMapping("/categories")
    public List<CategoryDto> getCategoriesController(@RequestParam(value = "from", required = false, defaultValue = "0") int from,
                                                     @RequestParam(value = "size", required = false, defaultValue = "10") int size) {
        return categoryService.getEntityService(null, null, from, size, BASIC);
    }

    @GetMapping("/events/{eventId}")
    public EventDto getEventByIdController(@PathVariable Long eventId, HttpServletRequest request) {
        statClientService.createHitStatClient(request, true);
        return eventService.getEntityByIdService(Map.of(GENERAL_ID, eventId), FULL, false);
    }

    @GetMapping("/events")
    public List<EventDto> getEventsController(@RequestParam(value = "text", required = false) String searchText,
                                              @RequestParam(value = "categories", required = false) List<Long> categoryIds,
                                              @RequestParam(value = "paid", required = false) Boolean isPaid,
                                              @RequestParam(value = "rangeStart", required = false) String rangeStart,
                                              @RequestParam(value = "rangeEnd", required = false, defaultValue = "9999-12-31 23:59:59") String rangeEnd,
                                              @RequestParam(value = "onlyAvailable", required = false, defaultValue = "false") Boolean isOnlyAvailable,
                                              @RequestParam(value = "from", required = false, defaultValue = "0") int from,
                                              @RequestParam(value = "size", required = false, defaultValue = "10") int size,
                                              @RequestParam(value = "sort", required = false, defaultValue = "EVENT_DATE") String sort,
                                              HttpServletRequest request) {
        statClientService.createHitStatClient(request, false);
        return eventService.getEventsForPublicService(searchText, categoryIds, isPaid, rangeStart, rangeEnd, isOnlyAvailable, from, size, sort);
    }

    @GetMapping("/compilations/{compId}")
    public CompilationDto getCompilationByIdController(@PathVariable long compId) {
        return compilationService.getEntityByIdService(Map.of(GENERAL_ID, compId), BASIC, true);
    }

    @GetMapping("/compilations")
    public List<CompilationDto> getCompilationsController(@RequestParam(value = "pinned", required = false) Boolean pinned,
                                                          @RequestParam(value = "from", required = false, defaultValue = "0") int from,
                                                          @RequestParam(value = "size", required = false, defaultValue = "10") int size) {
        return compilationService.getEntityService(null, null, from, size, BASIC, pinned);
    }
}
