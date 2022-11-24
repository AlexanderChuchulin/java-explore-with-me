package ewm.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import ewm.abstraction.EntityDto;
import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode(callSuper = false)
public class CategoryDto extends EntityDto {
    @JsonProperty("id")
    private Long categoryId;
    @JsonProperty("name")
    private String categoryName;

    @Override
    public Long getId() {
        return getCategoryId();
    }
}
