package ewm.controller;

import ewm.dto.UserDto;
import ewm.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin")
public class AdminController {
    private final UserService userService;

    @Autowired
    public AdminController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/users")
    public UserDto createUserController(@RequestBody UserDto userDto) {
        return userService.createEntityService(userDto);
    }

    @GetMapping("/users")
    public List<UserDto> getEntityController(@RequestParam(value = "ids", required = false) List<Long> ids,
                                             @RequestParam(value = "from", required = false, defaultValue = "0") int from,
                                             @RequestParam(value = "size", required = false, defaultValue = "10") int size) {
        return ids != null && ids.size() > 0 ? userService.getEntityByIdsService(ids) : userService.getEntityService(from, size);
    }

    @DeleteMapping("users/{userId}")
    public void deleteEntityByIdController(@PathVariable(required = false) long userId) {
        userService.deleteEntityByIdService(userId);
    }
}
