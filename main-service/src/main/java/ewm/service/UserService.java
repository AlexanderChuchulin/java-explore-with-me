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
    public void validateEntityService(User user, Boolean isUpdate, String conclusion) {
        StringBuilder excMsg = new StringBuilder();

        if (user.getUserName() == null || user.getUserName().isBlank()) {
            user.setUserName(user.getEmail());
        }

        // Проверка на правильность e-mail и наличие дубликатов во время создания и обновления пользователей
        if (user.getEmail() == null || !Pattern.compile("^[A-Z\\d._%+-]+@[A-Z\\d.-]+\\.[A-Z]{2,6}$",
                Pattern.CASE_INSENSITIVE).matcher(user.getEmail()).find()) {
            excMsg.append("e-mail must be in the correct format. ");
        } else if (userJpaRepository.findByEmailContainingIgnoreCase(user.getEmail()) != null) {
            excMsg.append(String.format("User with e-mail %s is already registered. ", user.getEmail()));
        }

        if (excMsg.length() > 0) {
            log.warn("User validation error. {}{}", excMsg, conclusion);
            if (excMsg.toString().contains("User with e-mail")) {
                throw new MainPropDuplicateExc(excMsg + conclusion);
            } else {
                throw new ValidationExc(excMsg + conclusion);
            }
        }
    }
}
