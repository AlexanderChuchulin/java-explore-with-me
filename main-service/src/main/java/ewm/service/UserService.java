package ewm.service;

import ewm.abstraction.EwmAbstractService;
import ewm.dto.UserDto;
import ewm.exception.MainPropDuplicateExc;
import ewm.exception.ValidationExc;
import ewm.mapper.UserMapper;
import ewm.model.User;
import ewm.repository.UserJpaRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.regex.Pattern;

@Service
@Slf4j
public class UserService extends EwmAbstractService<UserDto, User> {
    private final UserJpaRepository userJpaRepository;

    @Autowired
    public UserService(UserJpaRepository userJpaRepository, UserMapper userMapper) {
        name = "User";
        this.userJpaRepository = userJpaRepository;
        jpaRepository = userJpaRepository;
        mapper = userMapper;
    }

    @Override
    public void validateEntity(UserDto userDto, boolean isUpdate, boolean isAdmin, String conclusion, Long... params) {
        StringBuilder excReason = new StringBuilder();

        if (userDto.getUserName() == null || userDto.getUserName().isBlank()) {
            excReason.append("User name must be specified.");
        }

        if (userDto.getEmail() == null || !Pattern.compile("^[A-Z\\d._%+-]+@[A-Z\\d.-]+\\.[A-Z]{2,6}$",
                Pattern.CASE_INSENSITIVE).matcher(userDto.getEmail()).find()) {
            excReason.append("E-mail must be in the correct format. ");
        } else if (userJpaRepository.findByEmailContainingIgnoreCase(userDto.getEmail()) != null) {
            excReason.append(String.format("User with e-mail %s is already registered. ", userDto.getEmail()));
        }

        if (excReason.length() > 0) {
            log.warn("{} validation error. {}{}", name, excReason, conclusion);
            if (excReason.toString().contains("User with e-mail")) {
                throw new MainPropDuplicateExc(conclusion, excReason.toString());
            } else {
                throw new ValidationExc(conclusion, excReason.toString());
            }
        }
    }
}
