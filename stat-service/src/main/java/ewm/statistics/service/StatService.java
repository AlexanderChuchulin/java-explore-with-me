package ewm.statistics.service;

import ewm.statistics.dto.StatCountDto;
import ewm.statistics.dto.StatDto;
import ewm.statistics.mapper.StatMapper;
import ewm.statistics.repository.StatJpaRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@Slf4j
public class StatService {
    private final StatJpaRepository statJpaRepository;
    private final StatMapper statMapper;

    @Autowired
    public StatService(StatJpaRepository statJpaRepository, StatMapper statMapper) {
        this.statJpaRepository = statJpaRepository;
        this.statMapper = statMapper;
    }

    public List<StatCountDto> createHitStatService(StatDto statDto) {
        statJpaRepository.save(statMapper.dtoToStatModel(statDto));
        log.info("Create hit stats");
        return getStatService(null, null, List.of(statDto.getUri()), true);
    }

    public List<StatCountDto> getStatService(String start, String end, List<String> uris, Boolean uniqueIp) {
        if (start == null || start.isBlank()) {
            start = LocalDateTime.now().minusDays(1L).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        }
        if (end == null || end.isBlank()) {
            end = getCorrectDateTime(start).plusDays(1L).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        }

        log.info("Return stats by start {}, end {}, uris {}, uniqueIp {}", start, end, uris, uniqueIp);
        if (uniqueIp) {
            return statJpaRepository.findStatsByParamsAndUniqueIp(getCorrectDateTime(start), getCorrectDateTime(end), uris);
        } else {
            return statJpaRepository.findStatsByParams(getCorrectDateTime(start), getCorrectDateTime(end), uris);
        }
    }

    public LocalDateTime getCorrectDateTime(String textDateTime) {
        LocalDate parseDate = null;
        LocalTime parseTime = LocalTime.of(0, 0, 0);

        if (textDateTime.length() == 19) {
            try {
                return LocalDateTime.parse(textDateTime, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            } catch (Exception ignore) {
            }
        }

        if (textDateTime.length() == 10) {
            try {
                parseDate = LocalDate.parse(textDateTime, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            } catch (Exception ignore) {
            }
        }

        if (textDateTime.length() > 10 & textDateTime.length() <= 19) {
            try {
                parseDate = LocalDate.parse(textDateTime.split(" ")[0], DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            } catch (Exception ignore) {
            }

            try {
                parseTime = LocalTime.parse(textDateTime.split(" ")[1], DateTimeFormatter.ofPattern("HH:mm:ss"));
            } catch (Exception ignore) {
            }
        }

        if (parseDate != null) {
            return LocalDateTime.of(parseDate, parseTime);
        }
        throw new RuntimeException("Operation aborted - bad date and time format");
    }
}
