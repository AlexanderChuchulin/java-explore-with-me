package ewm.mapper;

import ewm.abstraction.EntityMapper;
import ewm.dto.UserDto;
import ewm.model.User;
import org.springframework.stereotype.Service;

@Service
public class UserMapper extends EntityMapper<UserDto, User> {

    @Override
    public User dtoToEntity(UserDto userDto) {
        return User.builder()
                .email(userDto.getEmail())
                .userName(userDto.getUserName())
                .build();
    }

    @Override
    public UserDto entityToDto(User user) {
        return UserDto.builder()
                .userId(user.getUserId())
                .email(user.getEmail())
                .userName(user.getUserName())
                .build();
    }
}
