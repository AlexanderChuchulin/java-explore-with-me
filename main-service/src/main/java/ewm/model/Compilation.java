package ewm.model;

import ewm.abstraction.EwmEntity;
import lombok.*;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "compilations")
@Builder
@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
@ToString
public class Compilation extends EwmEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "compilation_id")
    private Long compilationId;
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "event_compilation", joinColumns = {@JoinColumn(name = "compilation_id")}, inverseJoinColumns = {@JoinColumn(name = "event_id")})
    @ToString.Exclude
    private List<Event> eventsList;
    @JoinColumn(name = "pinned", nullable = false)
    boolean pinned;
    @JoinColumn(name = "compilation_title", nullable = false)
    String compilationTitle;
}
