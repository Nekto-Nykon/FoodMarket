package nykon.foodmarket.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserStatisticsDTO {
    private Long id;
    private String email;
    private String firstName;
    private String lastName;
    private String role;
    private Boolean isActive;
    private LocalDateTime createdAt;

    // Для Buyer
    private Integer totalOrders;
    private BigDecimal totalSpent;

    // Для Supplier
    private Integer totalOffers;
    private BigDecimal totalSales;
}
