package nykon.foodmarket.contoller;

import lombok.RequiredArgsConstructor;
import nykon.foodmarket.dto.ReviewDTO;
import nykon.foodmarket.dto.request.review.CreateReviewRequest;
import nykon.foodmarket.service.ReviewService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    /**
     * Створити відгук (для buyer)
     */
    @PostMapping
    public ResponseEntity<ReviewDTO> createReview(@RequestBody CreateReviewRequest request) {
        ReviewDTO review = reviewService.createReview(
                request.getOrderId(),
                request.getSupplierId(),
                request.getRating(),
                request.getComment()
        );
        return ResponseEntity.ok(review);
    }

    /**
     * Отримати відгуки постачальника
     */
    @GetMapping("/supplier/{supplierId}")
    public ResponseEntity<List<ReviewDTO>> getSupplierReviews(@PathVariable Long supplierId) {
        return ResponseEntity.ok(reviewService.getSupplierReviews(supplierId));
    }

    /**
     * Отримати мої відгуки (для buyer)
     */
    @GetMapping("/my-reviews")
    public ResponseEntity<List<ReviewDTO>> getMyReviews() {
        return ResponseEntity.ok(reviewService.getMyReviews());
    }

    /**
     * Отримати відгуки по замовленню
     */
    @GetMapping("/order/{orderId}")
    public ResponseEntity<List<ReviewDTO>> getOrderReviews(@PathVariable Long orderId) {
        return ResponseEntity.ok(reviewService.getOrderReviews(orderId));
    }


}
