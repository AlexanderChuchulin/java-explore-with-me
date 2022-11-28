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
public class UserDto extends EntityDto {
    @JsonProperty("id")
    private Long userId;
    private String email;
    @JsonProperty("name")
    private String userName;
    private String initiatorRating;

    @Override
    public Long getId() {
        return getUserId();
    }
}
