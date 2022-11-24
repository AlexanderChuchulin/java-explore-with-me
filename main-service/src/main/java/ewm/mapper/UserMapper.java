package ewm.mapper;

import ewm.abstraction.EntityMapper;
import ewm.dto.UserDto;
import ewm.model.User;
import ewm.other.DtoType;
import org.springframework.stereotype.Service;

@Service
public class UserMapper extends EntityMapper<UserDto, User> {

    @Override
    public User dtoToEntity(UserDto userDto, Long... params) {
        return User.builder()
                .userId(userDto.getUserId())
                .email(userDto.getEmail())
                .userName(userDto.getUserName())
                .build();
    }

    @Override
    public UserDto entityToDto(User user, DtoType... dtoType) {
        return UserDto.builder()
                .userId(user.getUserId())
                .email(user.getEmail())
                .userName(user.getUserName())
                .build();
    }
}
