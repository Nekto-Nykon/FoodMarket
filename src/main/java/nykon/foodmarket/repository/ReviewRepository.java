package nykon.foodmarket.repository;

import nykon.foodmarket.dto.ReviewDTO;
import nykon.foodmarket.model.Review;
import org.jspecify.annotations.Nullable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    List<Review> findBySupplierIdOrderByCreatedAtDesc(Long supplierId);


    List<Review> findByBuyerIdOrderByCreatedAtDesc(Long buyerId);


    Optional<Review> findByOrderId(Long orderId);


    boolean existsByOrderId(Long orderId);


    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.supplier.id = :supplierId")
    Double getAverageRatingBySupplier(@Param("supplierId") Long supplierId);


    long countBySupplierId(Long supplierId);


    List<Review> getAllByOrderId(Long orderId);

    List<Review> findByOrderIdOrderByCreatedAtDesc(Long orderId);

    boolean existsByOrderIdAndSupplierId(Long orderId, Long supplierId);
}