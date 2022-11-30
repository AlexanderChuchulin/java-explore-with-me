package ewm.client;

import ewm.repository.EventJpaRepository;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class StatClientService {
    private final String hitStatUri;
    private final EventJpaRepository eventJpaRepository;

    private final RestTemplate restTemplate;

    @Autowired
    public StatClientService(@Value("${ewm-stat-server.url}") String statServerUrl, EventJpaRepository eventJpaRepository) {
        this.eventJpaRepository = eventJpaRepository;
        hitStatUri = statServerUrl + "/hit";

        restTemplate = new RestTemplateBuilder().uriTemplateHandler(new DefaultUriBuilderFactory(statServerUrl + "/hit"))
                .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                .build();
    }

    @SneakyThrows
    public void createHitStatClient(HttpServletRequest request, boolean isNeedUpdate) {
        StatHitDto statHitDto = StatHitDto.builder()
                .app("ewm-main-service")
                .uri(request.getRequestURI())
                .ip(request.getRemoteAddr())
                .hitTimestamp(LocalDateTime.now())
                .build();

        ResponseEntity<StatCountDto[]> statServerResponse = restTemplate.exchange(hitStatUri, HttpMethod.POST,
                new HttpEntity<>(statHitDto), StatCountDto[].class);

        if (isNeedUpdate) {
            Arrays.stream(Objects.requireNonNull(statServerResponse.getBody()))
                    .forEach(stat -> eventJpaRepository.setViews(uriToEventId(stat.getUri()), stat.getHits()));
        }
    }

    long uriToEventId(String uri) {
        return Long.parseLong(uri.substring(uri.lastIndexOf("/") + 1));
    }
}
