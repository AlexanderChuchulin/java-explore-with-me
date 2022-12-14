package ewm.statistics.repository;

import ewm.statistics.dto.StatCountDto;
import ewm.statistics.model.StatModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface StatJpaRepository extends JpaRepository<StatModel, Long> {

    @Query("select new ewm.statistics.dto.StatCountDto(stat.app, stat.uri, count (stat.ip))" +
            "from StatModel as stat " +
            "where ((cast(:start as date) is null) or stat.hitTimestamp > :start) " +
            "and ((cast(:end as date) is null) or stat.hitTimestamp < :end) " +
            "and ((:uris) is null or stat.uri in :uris) " +
            "group by stat.uri, stat.app order by count (stat.uri) desc, stat.uri")
    List<StatCountDto> findStatsByParams(LocalDateTime start, LocalDateTime end, List<String> uris);

    @Query("select new ewm.statistics.dto.StatCountDto(stat.app, stat.uri, count (distinct stat.ip))" +
            "from StatModel as stat " +
            "where ((cast(:start as date) is null) or stat.hitTimestamp > :start) " +
            "and ((cast(:end as date) is null) or stat.hitTimestamp < :end) " +
            "and ((:uris) is null or stat.uri in :uris) " +
            "group by stat.uri, stat.app order by count (distinct stat.ip) desc, stat.uri")
    List<StatCountDto> findStatsByParamsAndUniqueIp(LocalDateTime start, LocalDateTime end, List<String> uris);
}
