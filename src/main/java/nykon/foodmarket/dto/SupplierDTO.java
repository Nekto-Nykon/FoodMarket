package nykon.foodmarket.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SupplierDTO {
    private Long id;
    private String companyName;
    private String address;
    private String taxId;
    private BigDecimal rating;
    private String description;
}
