package nykon.foodmarket.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OfferDTO {
    private Long id;
    private SupplierDTO supplier;
    private IngredientDTO ingredient;
    private BigDecimal price;
    private BigDecimal availableQuantity;
    private BigDecimal minOrderQuantity;
    private Boolean isActive;
    private LocalDate validFrom;
    private LocalDate validUntil;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
