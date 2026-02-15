package nykon.foodmarket.dto.request.ingredient;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateIngredientRequest {
    private String name;
    private String description;
    private String unitOfMeasure;
    private Long categoryId;
}