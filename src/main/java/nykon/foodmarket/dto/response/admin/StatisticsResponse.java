package nykon.foodmarket.dto.response.admin;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StatisticsResponse {
    private Long totalUsers;
    private Long activeUsers;
    private Long totalSuppliers;
    private Long totalOffers;
    private Long activeOffers;
    private Long totalOrders;
    private Long pendingOrders;
    private Long completedOrders;
}
