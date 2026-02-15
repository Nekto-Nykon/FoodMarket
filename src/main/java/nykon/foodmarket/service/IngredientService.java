package nykon.foodmarket.service;

import lombok.RequiredArgsConstructor;
import nykon.foodmarket.model.Ingredient;
import nykon.foodmarket.repository.IngredientRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class IngredientService {

    private final IngredientRepository ingredientRepository;

    @Transactional(readOnly = true)
    public List<Ingredient> getAllIngredients(Long categoryId) {
        if (categoryId != null) {
            return ingredientRepository.findByCategoryId(categoryId);
        }
        return ingredientRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Ingredient getIngredientById(Long id) {
        return ingredientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ingredient not found with id: " + id));
    }

    public Ingredient createIngredient(Ingredient ingredient) {
        return ingredientRepository.save(ingredient);
    }

    public Ingredient updateIngredient(Long id, Ingredient ingredient) {
        Ingredient existing = getIngredientById(id);
        existing.setName(ingredient.getName());
        existing.setDescription(ingredient.getDescription());
        existing.setCategory(ingredient.getCategory());
        existing.setUnitOfMeasure(ingredient.getUnitOfMeasure());
        return ingredientRepository.save(existing);
    }

    public void deleteIngredient(Long id) {
        if (!ingredientRepository.existsById(id)) {
            throw new RuntimeException("Ingredient not found with id: " + id);
        }
        ingredientRepository.deleteById(id);
    }
}