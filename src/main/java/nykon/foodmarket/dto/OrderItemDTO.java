package nykon.foodmarket.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemDTO {
    private Long id;
    private Long orderId;
    private OfferDTO offer;
    private BigDecimal quantity;
    private BigDecimal pricePerUnit;
    private BigDecimal subtotal;
}