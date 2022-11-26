package ewm.model;

import ewm.abstraction.EwmEntity;
import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "categories")
@Builder
@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
@ToString
public class Category extends EwmEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_id")
    private Long categoryId;
    @Column(name = "category_name", nullable = false, unique = true)
    private String categoryName;

}
