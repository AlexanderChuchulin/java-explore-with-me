package ewm.statistics.controller;

import ewm.statistics.dto.StatCountDto;
import ewm.statistics.dto.StatDto;
import ewm.statistics.service.StatService;
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
    public List<StatCountDto> createHitStat(@RequestBody StatDto statDto) {
        return statService.createHitStat(statDto);
    }

    @GetMapping("/stats")
    public List<StatCountDto> getStat(@RequestParam(value = "start", required = false) String start,
                                                @RequestParam(value = "end", required = false) String end,
                                                @RequestParam(value = "uris", required = false) List<String> uris,
                                                @RequestParam(value = "unique", defaultValue = "false") boolean unique) {
        return statService.getStat(start, end, uris, unique);
    }
}
