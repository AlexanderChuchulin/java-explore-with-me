package ewm.statistics.mapper;

import ewm.statistics.dto.StatDto;
import ewm.statistics.model.StatModel;
import org.springframework.stereotype.Service;

@Service
public class StatMapper {

    public StatModel dtoToStatModel(StatDto statDto) {
        return StatModel.builder()
                .app(statDto.getApp())
                .uri(statDto.getUri())
                .ip(statDto.getIp())
                .hitTimestamp(statDto.getHitTimestamp())
                .build();
    }
}
