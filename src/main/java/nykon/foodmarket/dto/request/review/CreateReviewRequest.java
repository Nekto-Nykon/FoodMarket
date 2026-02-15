package nykon.foodmarket.dto.request.review;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateReviewRequest {
    private Long orderId;
    private Long supplierId;
    private Integer rating;
    private String comment;
}
