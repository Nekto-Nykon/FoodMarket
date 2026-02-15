package nykon.foodmarket.mapper;

import lombok.RequiredArgsConstructor;
import nykon.foodmarket.dto.ReviewDTO;
import nykon.foodmarket.model.Review;
import nykon.foodmarket.repository.OrderRepository;
import nykon.foodmarket.repository.SupplierRepository;
import nykon.foodmarket.repository.UserRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ReviewMapper {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final SupplierRepository supplierRepository;

    /**
     * Entity -> DTO
     */
    public ReviewDTO toDTO(Review review) {
        if (review == null) {
            return null;
        }

        String buyerName = null;
        if (review.getBuyer() != null) {
            buyerName = review.getBuyer().getFirstName() + " " + review.getBuyer().getLastName();
        }

        return new ReviewDTO(
                review.getId(),
                review.getOrder() != null ? review.getOrder().getId() : null,
                review.getBuyer() != null ? review.getBuyer().getId() : null,
                buyerName,
                review.getSupplier() != null ? review.getSupplier().getId() : null,
                review.getSupplier() != null ? review.getSupplier().getCompanyName() : null,
                review.getRating(),
                review.getComment(),
                review.getCreatedAt()
        );
    }

    /**
     * DTO -> Entity
     */
    public Review toEntity(ReviewDTO dto) {
        if (dto == null) {
            return null;
        }

        Review review = new Review();
        review.setId(dto.getId());
        review.setRating(dto.getRating());
        review.setComment(dto.getComment());

        // Завантажуємо order з БД
        if (dto.getOrderId() != null) {
            orderRepository.findById(dto.getOrderId())
                    .ifPresent(review::setOrder);
        }

        // Завантажуємо buyer з БД
        if (dto.getBuyerId() != null) {
            userRepository.findById(dto.getBuyerId())
                    .ifPresent(review::setBuyer);
        }

        // Завантажуємо supplier з БД
        if (dto.getSupplierId() != null) {
            supplierRepository.findById(dto.getSupplierId())
                    .ifPresent(review::setSupplier);
        }

        return review;
    }

    /**
     * Update Entity from DTO
     */
    public void updateEntityFromDTO(ReviewDTO dto, Review review) {
        if (dto == null || review == null) {
            return;
        }

        review.setRating(dto.getRating());
        review.setComment(dto.getComment());
    }

    /**
     * List Entity -> List DTO
     */
    public List<ReviewDTO> toDTOList(List<Review> reviews) {
        if (reviews == null) {
            return null;
        }
        return reviews.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * List DTO -> List Entity
     */
    public List<Review> toEntityList(List<ReviewDTO> dtos) {
        if (dtos == null) {
            return null;
        }
        return dtos.stream()
                .map(this::toEntity)
                .collect(Collectors.toList());
    }
}