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
    public UserDto createUser(@RequestBody UserDto userDto) {
        return userService.createEntity(userDto, true);
    }

    @GetMapping("/users")
    public List<UserDto> getUsers(@RequestParam(value = "ids", required = false) List<Long> ids,
                                            @RequestParam(value = "from", required = false, defaultValue = "0") int from,
                                            @RequestParam(value = "size", required = false, defaultValue = "10") int size) {
        return userService.getEntity(null, ids, from, size, null);
    }

    @DeleteMapping("users/{userId}")
    public void deleteUserById(@PathVariable long userId) {
        userService.deleteEntityById(Map.of(GENERAL_ID, userId));
    }

    @PostMapping("/categories")
    public CategoryDto createCategory(@RequestBody CategoryDto categoryDto) {
        return categoryService.createEntity(categoryDto, true);
    }

    @PatchMapping("/categories")
    public CategoryDto updateCategory(@RequestBody CategoryDto categoryDto) {
        return categoryService.updateEntity(new HashMap<>(), categoryDto, true, true);
    }

    @DeleteMapping("/categories/{categoryId}")
    public void deleteCategoryById(@PathVariable long categoryId) {
        categoryService.deleteEntityById(Map.of(GENERAL_ID, categoryId));
    }

    @GetMapping("/events")
    public List<EventDto> getEventsByAdmin(@RequestParam(value = "users", required = false) List<Long> usersIds,
                                                   @RequestParam(value = "states", required = false) List<EventStatus> states,
                                                   @RequestParam(value = "categories", required = false) List<Long> categoryIds,
                                                   @RequestParam(value = "rangeStart", required = false, defaultValue = "0001-01-01 00:00:00") String rangeStart,
                                                   @RequestParam(value = "rangeEnd", required = false, defaultValue = "9999-12-31 23:59:59") String rangeEnd,
                                                   @RequestParam(value = "from", required = false, defaultValue = "0") int from,
                                                   @RequestParam(value = "size", required = false, defaultValue = "10") int size) {
        return eventService.getEventsForAdmin(usersIds, states, categoryIds, rangeStart, rangeEnd, from, size);
    }

    @PutMapping("/events/{eventId}")
    public EventDto updateEventByAdmin(@PathVariable Long eventId, @RequestBody EventDto eventDto) {
        return eventService.updateEntity(Map.of(GENERAL_ID, eventId), eventDto, true, true);
    }

    @PatchMapping("/events/{eventId}/publish")
    public EventDto publishEvent(@PathVariable long eventId) {
        return eventService.changeEventStatus(Map.of(GENERAL_ID, eventId), PUBLISHED, true);
    }

    @PatchMapping("/events/{eventId}/reject")
    public EventDto cancelEventByAdmin(@PathVariable long eventId) {
        return eventService.changeEventStatus(Map.of(GENERAL_ID, eventId), CANCELED, true);
    }

    @PostMapping("/compilations")
    public CompilationDto createCompilation(@RequestBody CompilationDto compilationDto) {
        return compilationService.createEntity(compilationDto, true);
    }

    @PatchMapping("/compilations/{compId}/pin")
    public void pinCompilation(@PathVariable long compId) {
        compilationService.compilationPinUpdate(compId, true);
    }

    @DeleteMapping("/compilations/{compId}/pin")
    public void unPinCompilation(@PathVariable long compId) {
        compilationService.compilationPinUpdate(compId, false);
    }

    @PatchMapping("/compilations/{compId}/events/{eventId}")
    public void addEventToCompilation(@PathVariable long compId, @PathVariable long eventId) {
        compilationService.compilationEventUpdate(compId, eventId,true);
    }

    @DeleteMapping("/compilations/{compId}/events/{eventId}")
    public void removeEventFromCompilation(@PathVariable long compId, @PathVariable long eventId) {
        compilationService.compilationEventUpdate(compId, eventId,false);
    }

    @DeleteMapping("/compilations/{compId}")
    public void deleteCompilation(@PathVariable long compId) {
        compilationService.deleteEntityById(Map.of(GENERAL_ID, compId));
    }

}
