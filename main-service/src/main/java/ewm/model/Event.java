package ewm.model;

import ewm.abstraction.EwmEntity;
import ewm.other.EventStatus;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "events")
@Builder
@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
@ToString
public class Event extends EwmEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "event_id")
    private Long eventId;
    @Column(name = "annotation", nullable = false)
    private String annotation;
    @ManyToOne
    @JoinColumn(name = "category_id", referencedColumnName = "category_id", nullable = false)
    Category category;
    @Column(name = "confirmed_requests")
    private Long confirmedRequests;
    @Column(name = "description", nullable = false)
    private String description;
    @Column(name = "event_created", nullable = false)
    private LocalDateTime eventCreated;
    @Column(name = "event_date_start", nullable = false)
    private LocalDateTime eventDate;
    @Column(name = "event_date_end", nullable = false)
    private LocalDateTime eventDateEnd;
    @Column(name = "event_status", nullable = false)
    @Enumerated(EnumType.STRING)
    private EventStatus eventStatus;
    @Column(name = "event_title", nullable = false)
    private String eventTitle;
    @ManyToOne
    @JoinColumn(name = "initiator_id", referencedColumnName = "user_id", nullable = false)
    private User initiator;
    @Column(name = "location_latitude", nullable = false)
    private Double eventLatitude;
    @Column(name = "location_longitude", nullable = false)
    private Double eventLongitude;
    @Column(name = "paid", nullable = false)
    private Boolean paid;
    @Column(name = "participant_limit", nullable = false)
    private Integer participantLimit;
    @Column(name = "published")
    private LocalDateTime published;
    @Column(name = "request_moderation", nullable = false)
    private Boolean requestModeration;
    private Long views;
    private Double eventRating;
    private Double partOfVoted;
}
