package ewm.repository;

import ewm.abstraction.EwmJpaRepository;
import ewm.model.User;
import org.springframework.stereotype.Repository;

@Repository
public interface UserJpaRepository extends EwmJpaRepository<User> {

    User findByEmailContainingIgnoreCase(String emailSearch);
}
