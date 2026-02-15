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

    /**
     * Знайти відгуки по supplier ID
     */
    List<Review> findBySupplierIdOrderByCreatedAtDesc(Long supplierId);

    /**
     * Знайти відгуки по buyer ID
     */
    List<Review> findByBuyerIdOrderByCreatedAtDesc(Long buyerId);

    /**
     * Знайти відгук по order ID
     */
    Optional<Review> findByOrderId(Long orderId);

    /**
     * Перевірити чи існує відгук для замовлення
     */
    boolean existsByOrderId(Long orderId);

    /**
     * Середній рейтинг постачальника
     */
    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.supplier.id = :supplierId")
    Double getAverageRatingBySupplier(@Param("supplierId") Long supplierId);

    /**
     * Кількість відгуків постачальника
     */
    long countBySupplierId(Long supplierId);


    List<Review> getAllByOrderId(Long orderId);

    List<Review> findByOrderIdOrderByCreatedAtDesc(Long orderId);

    boolean existsByOrderIdAndSupplierId(Long orderId, Long supplierId);
}