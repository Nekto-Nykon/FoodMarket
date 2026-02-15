package nykon.foodmarket.mapper;

import lombok.RequiredArgsConstructor;
import nykon.foodmarket.dto.IngredientDTO;
import nykon.foodmarket.model.Ingredient;
import nykon.foodmarket.repository.CategoryRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class IngredientMapper {

    private final CategoryMapper categoryMapper;
    private final CategoryRepository categoryRepository;

    /**
     * Entity -> DTO
     */
    public IngredientDTO toDTO(Ingredient ingredient) {
        if (ingredient == null) {
            return null;
        }

        return new IngredientDTO(
                ingredient.getId(),
                ingredient.getName(),
                ingredient.getDescription(),
                ingredient.getUnitOfMeasure(),
                categoryMapper.toDTO(ingredient.getCategory())
        );
    }

    /**
     * DTO -> Entity
     */
    public Ingredient toEntity(IngredientDTO dto) {
        if (dto == null) {
            return null;
        }

        Ingredient ingredient = new Ingredient();
        ingredient.setId(dto.getId());
        ingredient.setName(dto.getName());
        ingredient.setDescription(dto.getDescription());
        ingredient.setUnitOfMeasure(dto.getUnitOfMeasure());

        // Завантажуємо категорію з БД якщо вказана
        if (dto.getCategory() != null && dto.getCategory().getId() != null) {
            categoryRepository.findById(dto.getCategory().getId())
                    .ifPresent(ingredient::setCategory);
        }

        return ingredient;
    }

    /**
     * Update Entity from DTO
     */
    public void updateEntityFromDTO(IngredientDTO dto, Ingredient ingredient) {
        if (dto == null || ingredient == null) {
            return;
        }

        ingredient.setName(dto.getName());
        ingredient.setDescription(dto.getDescription());
        ingredient.setUnitOfMeasure(dto.getUnitOfMeasure());

        if (dto.getCategory() != null && dto.getCategory().getId() != null) {
            categoryRepository.findById(dto.getCategory().getId())
                    .ifPresent(ingredient::setCategory);
        }
    }

    /**
     * List Entity -> List DTO
     */
    public List<IngredientDTO> toDTOList(List<Ingredient> ingredients) {
        if (ingredients == null) {
            return null;
        }
        return ingredients.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * List DTO -> List Entity
     */
    public List<Ingredient> toEntityList(List<IngredientDTO> dtos) {
        if (dtos == null) {
            return null;
        }
        return dtos.stream()
                .map(this::toEntity)
                .collect(Collectors.toList());
    }
}
