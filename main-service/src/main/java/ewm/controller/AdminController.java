package ewm.controller;

import ewm.dto.CategoryDto;
import ewm.dto.CompilationDto;
import ewm.dto.EventDto;
import ewm.dto.UserDto;
import ewm.other.EventStatus;
import ewm.service.CategoryService;
import ewm.service.CompilationService;
import ewm.service.EventService;
import ewm.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static ewm.other.IdName.GENERAL_ID;
import static ewm.other.EventStatus.CANCELED;
import static ewm.other.EventStatus.PUBLISHED;

@RestController
@RequestMapping("/admin")
public class AdminController {
    private final CategoryService categoryService;
    private final CompilationService compilationService;
    private final EventService eventService;
    private final UserService userService;

    @Autowired
    public AdminController(CategoryService categoryService, CompilationService compilationService,
                           EventService eventService, UserService userService) {
        this.categoryService = categoryService;
        this.compilationService = compilationService;
        this.eventService = eventService;
        this.userService = userService;
    }

    @PostMapping("/users")
    public UserDto createUserController(@RequestBody UserDto userDto) {
        return userService.createEntityService(userDto, true);
    }

    @GetMapping("/users")
    public List<UserDto> getUsersController(@RequestParam(value = "ids", required = false) List<Long> ids,
                                            @RequestParam(value = "from", required = false, defaultValue = "0") int from,
                                            @RequestParam(value = "size", required = false, defaultValue = "10") int size) {
        return userService.getEntityService(null, ids, from, size, null);
    }

    @DeleteMapping("users/{userId}")
    public void deleteUserByIdController(@PathVariable long userId) {
        userService.deleteEntityByIdService(Map.of(GENERAL_ID, userId));
    }

    @PostMapping("/categories")
    public CategoryDto createCategoryController(@RequestBody CategoryDto categoryDto) {
        return categoryService.createEntityService(categoryDto, true);
    }

    @PatchMapping("/categories")
    public CategoryDto updateCategoryController(@RequestBody CategoryDto categoryDto) {
        return categoryService.updateEntityService(new HashMap<>(), categoryDto, true, true);
    }

    @DeleteMapping("/categories/{categoryId}")
    public void deleteCategoryByIdController(@PathVariable long categoryId) {
        categoryService.deleteEntityByIdService(Map.of(GENERAL_ID, categoryId));
    }

    @GetMapping("/events")
    public List<EventDto> getEventsAdminController(@RequestParam(value = "users", required = false) List<Long> usersIds,
                                                   @RequestParam(value = "states", required = false) List<EventStatus> states,
                                                   @RequestParam(value = "categories", required = false) List<Long> categoryIds,
                                                   @RequestParam(value = "rangeStart", required = false, defaultValue = "0001-01-01 00:00:00") String rangeStart,
                                                   @RequestParam(value = "rangeEnd", required = false, defaultValue = "9999-12-31 23:59:59") String rangeEnd,
                                                   @RequestParam(value = "from", required = false, defaultValue = "0") int from,
                                                   @RequestParam(value = "size", required = false, defaultValue = "10") int size) {
        return eventService.getEventsForAdminService(usersIds, states, categoryIds, rangeStart, rangeEnd, from, size);
    }

    @PutMapping("/events/{eventId}")
    public EventDto updateEventAdminController(@PathVariable Long eventId, @RequestBody EventDto eventDto) {
        return eventService.updateEntityService(Map.of(GENERAL_ID, eventId), eventDto, true, true);
    }

    @PatchMapping("/events/{eventId}/publish")
    public EventDto publishEventController(@PathVariable long eventId) {
        return eventService.changeEventStatusService(Map.of(GENERAL_ID, eventId), PUBLISHED, true);
    }

    @PatchMapping("/events/{eventId}/reject")
    public EventDto cancelEventAdminController(@PathVariable long eventId) {
        return eventService.changeEventStatusService(Map.of(GENERAL_ID, eventId), CANCELED, true);
    }

    @PostMapping("/compilations")
    public CompilationDto createCompilationController(@RequestBody CompilationDto compilationDto) {
        return compilationService.createEntityService(compilationDto, true);
    }

    @PatchMapping("/compilations/{compId}/pin")
    public void pinCompilationController(@PathVariable long compId) {
        compilationService.compilationPinUpdate(compId, true);
    }

    @DeleteMapping("/compilations/{compId}/pin")
    public void unPinCompilationController(@PathVariable long compId) {
        compilationService.compilationPinUpdate(compId, false);
    }

    @PatchMapping("/compilations/{compId}/events/{eventId}")
    public void addEventToCompilationController(@PathVariable long compId, @PathVariable long eventId) {
        compilationService.compilationEventUpdate(compId, eventId,true);
    }

    @DeleteMapping("/compilations/{compId}/events/{eventId}")
    public void removeEventFromCompilationController(@PathVariable long compId, @PathVariable long eventId) {
        compilationService.compilationEventUpdate(compId, eventId,false);
    }

    @DeleteMapping("/compilations/{compId}")
    public void deleteCompilationController(@PathVariable long compId) {
        compilationService.deleteEntityByIdService(Map.of(GENERAL_ID, compId));
    }

}
