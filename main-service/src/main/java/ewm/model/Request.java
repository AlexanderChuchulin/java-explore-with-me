package ewm.model;

import ewm.abstraction.EwmEntity;
import ewm.other.RequestStatus;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "requests")
@Builder
@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
@ToString
public class Request extends EwmEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "request_id")
    private Long requestId;
    @Column(name = "request_created", nullable = false)
    private LocalDateTime requestCreated;
    @ManyToOne
    @JoinColumn(name = "event_id", referencedColumnName = "event_id", nullable = false)
    Event event;
    @ManyToOne
    @JoinColumn(name = "requester_id", referencedColumnName = "user_id", nullable = false)
    private User requester;
    @Column(name = "request_status", nullable = false)
    @Enumerated(EnumType.STRING)
    RequestStatus requestStatus;
    @Column(name = "rating_from_requester")
    private Integer ratingFromRequester;
}
