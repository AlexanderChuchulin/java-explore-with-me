package ewm.mapper;

import ewm.abstraction.EntityMapper;
import ewm.dto.CategoryDto;
import ewm.model.Category;
import ewm.other.DtoType;
import org.springframework.stereotype.Service;

@Service
public class CategoryMapper extends EntityMapper<CategoryDto, Category> {
    @Override
    public Category dtoToEntity(CategoryDto categoryDto, Long... params) {
        return Category.builder()
                .categoryId(categoryDto.getCategoryId())
                .categoryName(categoryDto.getCategoryName())
                .build();
    }

    @Override
    public CategoryDto entityToDto(Category category, DtoType... dtoType) {
        return CategoryDto.builder()
                .categoryId(category.getCategoryId())
                .categoryName(category.getCategoryName())
                .build();
    }
}
