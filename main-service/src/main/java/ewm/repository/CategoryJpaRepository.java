package ewm.repository;

import ewm.abstraction.EwmJpaRepository;
import ewm.model.Category;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryJpaRepository extends EwmJpaRepository<Category> {

    Category findByCategoryNameContainingIgnoreCase(String categoryNameSearch);
}
