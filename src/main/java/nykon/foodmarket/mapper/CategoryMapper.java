package nykon.foodmarket.mapper;

import nykon.foodmarket.dto.CategoryDTO;
import nykon.foodmarket.model.Category;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class CategoryMapper {

    /**
     * Entity -> DTO
     */
    public CategoryDTO toDTO(Category category) {
        if (category == null) {
            return null;
        }

        return new CategoryDTO(
                category.getId(),
                category.getName(),
                category.getDescription()
        );
    }

    /**
     * DTO -> Entity
     */
    public Category toEntity(CategoryDTO dto) {
        if (dto == null) {
            return null;
        }

        Category category = new Category();
        category.setId(dto.getId());
        category.setName(dto.getName());
        category.setDescription(dto.getDescription());
        return category;
    }

    /**
     * Update Entity from DTO
     */
    public void updateEntityFromDTO(CategoryDTO dto, Category category) {
        if (dto == null || category == null) {
            return;
        }

        category.setName(dto.getName());
        category.setDescription(dto.getDescription());
    }

    /**
     * List Entity -> List DTO
     */
    public List<CategoryDTO> toDTOList(List<Category> categories) {
        if (categories == null) {
            return null;
        }
        return categories.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * List DTO -> List Entity
     */
    public List<Category> toEntityList(List<CategoryDTO> dtos) {
        if (dtos == null) {
            return null;
        }
        return dtos.stream()
                .map(this::toEntity)
                .collect(Collectors.toList());
    }
}
