package ewm.model;

import ewm.abstraction.EwmEntity;
import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "users")
@Builder
@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
@ToString
public class User extends EwmEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId;
    @Column(name = "email", nullable = false, unique = true)
    private String email;
    @Column(name = "user_name", nullable = false)
    private String userName;
    private Double initiatorRating;
}
