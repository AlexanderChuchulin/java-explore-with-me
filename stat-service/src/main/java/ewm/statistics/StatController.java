package ewm.statistics;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class StatController {
    private final StatService statService;

    @Autowired
    public StatController(StatService statService) {
        this.statService = statService;
    }

    @PostMapping("/hit")
    public List<StatCountDto> createHitStatController(@RequestBody StatDto statDto) {
        return statService.createHitStatService(statDto);
    }

    @GetMapping("/stats")
    public List<StatCountDto> getStatController(@RequestParam(value = "start", required = false) String start,
                                                @RequestParam(value = "end", required = false) String end,
                                                @RequestParam(value = "uris", required = false) List<String> uris,
                                                @RequestParam(value = "unique", defaultValue = "false") boolean unique) {
        return statService.getStatService(start, end, uris, unique);
    }
}
