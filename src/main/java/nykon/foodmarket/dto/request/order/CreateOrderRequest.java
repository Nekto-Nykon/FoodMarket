package nykon.foodmarket.dto.request.order;

import nykon.foodmarket.model.OrderItem;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateOrderRequest {
    private String deliveryAddress;
    private List<OrderItemRequest> items;
}