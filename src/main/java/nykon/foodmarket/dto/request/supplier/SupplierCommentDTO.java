package nykon.foodmarket.dto.request.supplier;


import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SupplierCommentDTO {
    private Long id;
    private Long supplierId;
    private Long adminId;
    private String adminEmail;
    private String comment;
    private LocalDateTime createdAt;
}