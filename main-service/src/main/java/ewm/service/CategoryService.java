package ewm.service;

import ewm.abstraction.EwmAbstractService;
import ewm.dto.CategoryDto;
import ewm.exception.MainPropDuplicateExc;
import ewm.exception.ValidationExc;
import ewm.mapper.CategoryMapper;
import ewm.model.Category;
import ewm.repository.CategoryJpaRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class CategoryService extends EwmAbstractService<CategoryDto, Category> {
    private final CategoryJpaRepository categoryJpaRepository;

    public CategoryService(CategoryJpaRepository categoryJpaRepository, CategoryMapper categoryMapper) {
        name = "Category";
        this.categoryJpaRepository = categoryJpaRepository;
        jpaRepository = categoryJpaRepository;
        mapper = categoryMapper;
    }

    @Override
    public void validateEntityService(CategoryDto categoryDto, boolean isUpdate, boolean isAdmin, String conclusion, Long... params) {
        StringBuilder excReason = new StringBuilder();

        if (categoryDto.getCategoryName() == null || categoryDto.getCategoryName().isBlank()) {
            excReason.append("Category must be specified. ");
        }
        if (categoryJpaRepository.findByCategoryNameContainingIgnoreCase(categoryDto.getCategoryName()) != null) {
            excReason.append(String.format("Category with name %s is already exist. ", categoryDto.getCategoryName()));
        }

        if (excReason.length() > 0) {
            log.warn("{} validation error. {}{}", name, excReason, conclusion);
            if (excReason.toString().contains("Category with name")) {
                throw new MainPropDuplicateExc(conclusion, excReason.toString());
            } else {
                throw new ValidationExc(conclusion, excReason.toString());
            }
        }
    }
}
